package hue

import (
	"encoding/json"
	"testing"
)

func TestIsLight(t *testing.T) {
	tests := []struct {
		name     string
		resource Resource
		want     bool
	}{
		{"light", Resource{Type: "light"}, true},
		{"button", Resource{Type: "button"}, false},
		{"empty", Resource{}, false},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := IsLight(tt.resource); got != tt.want {
				t.Errorf("IsLight() = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestIsButton(t *testing.T) {
	if !IsButton(Resource{Type: "button"}) {
		t.Error("expected true for button")
	}
	if IsButton(Resource{Type: "light"}) {
		t.Error("expected false for light")
	}
}

func TestIsMotion(t *testing.T) {
	if !IsMotion(Resource{Type: "motion"}) {
		t.Error("expected true")
	}
}

func TestIsTemperature(t *testing.T) {
	if !IsTemperature(Resource{Type: "temperature"}) {
		t.Error("expected true")
	}
}

func TestIsLightLevel(t *testing.T) {
	if !IsLightLevel(Resource{Type: "light_level"}) {
		t.Error("expected true")
	}
}

func TestIsDevicePower(t *testing.T) {
	if !IsDevicePower(Resource{Type: "device_power"}) {
		t.Error("expected true")
	}
}

func TestIsGroupedLight(t *testing.T) {
	if !IsGroupedLight(Resource{Type: "grouped_light"}) {
		t.Error("expected true")
	}
}

func TestIsZigbeeConnectivity(t *testing.T) {
	if !IsZigbeeConnectivity(Resource{Type: "zigbee_connectivity"}) {
		t.Error("expected true")
	}
}

func TestIsZgpConnectivity(t *testing.T) {
	if !IsZgpConnectivity(Resource{Type: "zgp_connectivity"}) {
		t.Error("expected true")
	}
}

func TestIsRoom(t *testing.T) {
	if !IsRoom(Resource{Type: "room"}) {
		t.Error("expected true")
	}
}

func TestIsTrigger(t *testing.T) {
	if !IsTrigger(Resource{Type: "button"}) {
		t.Error("expected button to be trigger")
	}
	if !IsTrigger(Resource{Type: "motion"}) {
		t.Error("expected motion to be trigger")
	}
	if IsTrigger(Resource{Type: "light"}) {
		t.Error("expected light to not be trigger")
	}
}

func TestIsNameable(t *testing.T) {
	if IsNameable(Resource{}) {
		t.Error("expected false for no metadata")
	}
	if IsNameable(Resource{Metadata: &Metadata{}}) {
		t.Error("expected false for empty name")
	}
	if !IsNameable(Resource{Metadata: &Metadata{Name: "test"}}) {
		t.Error("expected true for named resource")
	}
}

func TestResourceUnmarshalJSON_Light(t *testing.T) {
	data := `{
		"type": "light",
		"id": "abc-123",
		"metadata": {"name": "Desk Lamp", "archetype": "sultan_bulb"},
		"on": {"on": true},
		"dimming": {"brightness": 75.5},
		"color_temperature": {"mirek": 350, "mirek_valid": true}
	}`

	var r Resource
	if err := json.Unmarshal([]byte(data), &r); err != nil {
		t.Fatal(err)
	}

	if r.Type != "light" {
		t.Errorf("expected type light, got %s", r.Type)
	}
	if r.ID != "abc-123" {
		t.Errorf("expected id abc-123, got %s", r.ID)
	}
	if r.Metadata == nil || r.Metadata.Name != "Desk Lamp" {
		t.Error("expected metadata name Desk Lamp")
	}
	if r.On == nil || !r.On.On {
		t.Error("expected on=true")
	}
	if r.Dimming == nil || r.Dimming.Brightness != 75.5 {
		t.Errorf("expected brightness 75.5, got %v", r.Dimming)
	}
	if r.ColorTemperature == nil || r.ColorTemperature.Mirek == nil || *r.ColorTemperature.Mirek != 350 {
		t.Error("expected mirek 350")
	}
}

func TestResourceUnmarshalJSON_Button(t *testing.T) {
	data := `{
		"type": "button",
		"id": "btn-1",
		"metadata": {"control_id": 3},
		"button": {"last_event": "short_release"}
	}`

	var r Resource
	if err := json.Unmarshal([]byte(data), &r); err != nil {
		t.Fatal(err)
	}

	if r.Type != "button" {
		t.Errorf("expected type button, got %s", r.Type)
	}
	if r.ButtonMetadata == nil || r.ButtonMetadata.ControlID != 3 {
		t.Errorf("expected control_id 3, got %v", r.ButtonMetadata)
	}
	if r.Button == nil || r.Button.LastEvent != "short_release" {
		t.Error("expected last_event short_release")
	}
}

func TestResourceUnmarshalJSON_Motion(t *testing.T) {
	data := `{
		"type": "motion",
		"id": "mot-1",
		"enabled": true,
		"motion": {"motion": true, "motion_valid": true}
	}`

	var r Resource
	if err := json.Unmarshal([]byte(data), &r); err != nil {
		t.Fatal(err)
	}

	if r.Motion == nil || !r.Motion.Motion {
		t.Error("expected motion=true")
	}
}

func TestResourceUnmarshalJSON_Room(t *testing.T) {
	data := `{
		"type": "room",
		"id": "room-1",
		"metadata": {"name": "Office", "archetype": "office"},
		"children": [{"rid": "dev-1", "rtype": "device"}],
		"services": [{"rid": "light-1", "rtype": "light"}]
	}`

	var r Resource
	if err := json.Unmarshal([]byte(data), &r); err != nil {
		t.Fatal(err)
	}

	if !IsRoom(r) {
		t.Error("expected room type")
	}
	if len(r.Children) != 1 || r.Children[0].RID != "dev-1" {
		t.Errorf("expected 1 child, got %v", r.Children)
	}
	if len(r.Services) != 1 || r.Services[0].RID != "light-1" {
		t.Errorf("expected 1 service, got %v", r.Services)
	}
}

func TestHueEventUnmarshal(t *testing.T) {
	data := `{
		"creationtime": "2024-01-01T00:00:00Z",
		"id": "evt-1",
		"type": "update",
		"data": [
			{"type": "light", "id": "light-1", "on": {"on": true}}
		]
	}`

	var evt HueEvent
	if err := json.Unmarshal([]byte(data), &evt); err != nil {
		t.Fatal(err)
	}

	if evt.ID != "evt-1" {
		t.Errorf("expected id evt-1, got %s", evt.ID)
	}
	if len(evt.Data) != 1 {
		t.Fatalf("expected 1 data item, got %d", len(evt.Data))
	}
	if evt.Data[0].On == nil || !evt.Data[0].On.On {
		t.Error("expected on=true in event data")
	}
}
