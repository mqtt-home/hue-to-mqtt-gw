package hue

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/mqtt-home/hue2mqtt/config"
)

func setupTestServer(handler http.HandlerFunc) (*httptest.Server, func()) {
	server := httptest.NewTLSServer(handler)
	return server, server.Close
}

func TestFetchDevices(t *testing.T) {
	response := Result{
		Data: []Resource{
			{Type: "device", ID: "dev-1", Metadata: &Metadata{Name: "Test Device"}},
		},
	}
	data, _ := json.Marshal(response)

	server, cleanup := setupTestServer(func(w http.ResponseWriter, r *http.Request) {
		if r.Header.Get("hue-application-key") != "test-key" {
			t.Error("expected api key header")
		}
		if r.URL.Path != "/clip/v2/resource/device" {
			t.Errorf("expected /clip/v2/resource/device, got %s", r.URL.Path)
		}
		w.Write(data)
	})
	defer cleanup()

	// Parse server URL to get host:port
	cfg := config.HueConfig{
		Protocol: "https",
		APIKey:   "test-key",
	}
	// Override the client to use the test server
	InitClient(cfg)
	baseURL = server.URL + "/clip/v2"
	client = server.Client()

	result, err := FetchDevices()
	if err != nil {
		t.Fatal(err)
	}
	if len(result.Data) != 1 {
		t.Fatalf("expected 1 device, got %d", len(result.Data))
	}
	if result.Data[0].ID != "dev-1" {
		t.Errorf("expected dev-1, got %s", result.Data[0].ID)
	}
}

func TestFetchRooms(t *testing.T) {
	response := Result{
		Data: []Resource{
			{
				Type:     "room",
				ID:       "room-1",
				Metadata: &Metadata{Name: "Office"},
				Children: []ResourceRef{{RID: "dev-1", RType: "device"}},
			},
		},
	}
	data, _ := json.Marshal(response)

	server, cleanup := setupTestServer(func(w http.ResponseWriter, r *http.Request) {
		w.Write(data)
	})
	defer cleanup()

	InitClient(config.HueConfig{Protocol: "https", APIKey: "key"})
	baseURL = server.URL + "/clip/v2"
	client = server.Client()

	result, err := FetchRooms()
	if err != nil {
		t.Fatal(err)
	}
	if len(result.Data) != 1 || result.Data[0].Metadata.Name != "Office" {
		t.Errorf("unexpected result: %v", result)
	}
}

func TestCleanPutMessage(t *testing.T) {
	// Empty color_temperature should be removed
	mirek := 350
	msg := PutMessage{
		On:               &LightOnOffData{On: true},
		ColorTemperature: &LightColorTemperatureData{Mirek: &mirek, MirekValid: true},
		Color:            &LightColorData{XY: ColorXY{X: 0.3, Y: 0.4}},
	}

	cleaned := cleanPutMessage(msg)
	// When both present, color should be removed
	if cleaned.Color != nil {
		t.Error("expected color to be removed when color_temperature present")
	}
	if cleaned.ColorTemperature == nil {
		t.Error("expected color_temperature to be kept")
	}

	// Nil mirek should remove color_temperature
	msg2 := PutMessage{
		ColorTemperature: &LightColorTemperatureData{Mirek: nil},
	}
	cleaned2 := cleanPutMessage(msg2)
	if cleaned2.ColorTemperature != nil {
		t.Error("expected nil mirek to remove color_temperature")
	}
}
