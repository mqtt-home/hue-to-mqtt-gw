package hue

import (
	"bytes"
	"crypto/tls"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"sync"

	"github.com/mqtt-home/hue2mqtt/config"
	"github.com/philipparndt/go-logger"
)

var (
	client  *http.Client
	baseURL string
	apiKey  string
	putMu   sync.Mutex
)

func InitClient(cfg config.HueConfig) {
	client = &http.Client{
		Transport: &http.Transport{
			TLSClientConfig: &tls.Config{
				InsecureSkipVerify: true,
			},
		},
	}
	baseURL = fmt.Sprintf("%s://%s:%d/clip/v2", cfg.Protocol, cfg.Host, cfg.Port)
	apiKey = cfg.APIKey
}

func doGet(endpoint string) ([]byte, error) {
	url := baseURL + "/" + endpoint
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, err
	}
	req.Header.Set("hue-application-key", apiKey)
	req.Header.Set("Accept", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("GET %s: %w", url, err)
	}
	defer resp.Body.Close()

	return io.ReadAll(resp.Body)
}

func fetchResources(endpoint string) (*Result, error) {
	data, err := doGet(endpoint)
	if err != nil {
		logger.Error("Error fetching", "endpoint", endpoint, "error", err)
		return nil, err
	}

	logger.Trace("Fetched response", "endpoint", endpoint, "length", len(data))

	var result Result
	if err := json.Unmarshal(data, &result); err != nil {
		logger.Error("Error parsing response", "endpoint", endpoint, "error", err)
		return nil, err
	}

	// Count resource types
	typeCounts := make(map[string]int)
	for _, r := range result.Data {
		typeCounts[r.Type]++
	}
	logger.Debug("Fetched resources", "endpoint", endpoint, "total", len(result.Data), "types", typeCounts)

	return &result, nil
}

func FetchDevices() (*Result, error) {
	return fetchResources("resource/device")
}

func FetchRooms() (*Result, error) {
	return fetchResources("resource/room")
}

func FetchAllResources() (*Result, error) {
	return fetchResources("resource")
}

func FetchTyped(resourceType string) (*Result, error) {
	return fetchResources("resource/" + resourceType)
}

func FetchTypedByID(resourceType string, id string) (*Resource, error) {
	result, err := fetchResources(fmt.Sprintf("resource/%s/%s", resourceType, id))
	if err != nil {
		return nil, err
	}
	if len(result.Data) == 1 {
		return &result.Data[0], nil
	}
	return nil, fmt.Errorf("expected 1 result, got %d", len(result.Data))
}

func doPut(endpoint string, body interface{}) error {
	url := baseURL + "/" + endpoint
	data, err := json.Marshal(body)
	if err != nil {
		return err
	}

	req, err := http.NewRequest("PUT", url, bytes.NewReader(data))
	if err != nil {
		return err
	}
	req.Header.Set("hue-application-key", apiKey)
	req.Header.Set("Accept", "application/json")
	req.Header.Set("Content-Type", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("PUT %s: %w", url, err)
	}
	defer resp.Body.Close()

	if resp.StatusCode >= 400 {
		respBody, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("PUT %s returned %d: %s", url, resp.StatusCode, string(respBody))
	}

	return nil
}

// PutMessage represents the body sent to Hue for light updates
type PutMessage struct {
	On               *LightOnOffData            `json:"on,omitempty"`
	Dimming          *LightDimmingData           `json:"dimming,omitempty"`
	ColorTemperature *LightColorTemperatureData  `json:"color_temperature,omitempty"`
	Color            *LightColorData             `json:"color,omitempty"`
}

func cleanPutMessage(msg PutMessage) PutMessage {
	// Remove empty color_temperature
	if msg.ColorTemperature != nil && msg.ColorTemperature.Mirek == nil {
		msg.ColorTemperature = nil
	}

	// If both color_temperature and color present, prefer color_temperature
	if msg.ColorTemperature != nil && msg.Color != nil {
		msg.Color = nil
	}

	return msg
}

func PutLightResource(resource Resource) error {
	putMu.Lock()
	defer putMu.Unlock()

	msg := cleanPutMessage(PutMessage{
		On:               resource.On,
		Dimming:          resource.Dimming,
		ColorTemperature: resource.ColorTemperature,
		Color:            resource.Color,
	})

	endpoint := fmt.Sprintf("resource/%s/%s", resource.Type, resource.ID)
	logger.Debug("PutLightResource", "endpoint", endpoint, "message", msg)
	return doPut(endpoint, msg)
}

func PutGroupedLightResource(resource Resource) error {
	putMu.Lock()
	defer putMu.Unlock()

	msg := cleanPutMessage(PutMessage{
		On:               resource.On,
		Dimming:          resource.Dimming,
		ColorTemperature: resource.ColorTemperature,
		Color:            resource.Color,
	})

	endpoint := fmt.Sprintf("resource/%s/%s", resource.Type, resource.ID)
	logger.Debug("PutGroupedLightResource", "endpoint", endpoint, "message", msg)
	return doPut(endpoint, msg)
}

func PutLight(resource Resource, msg PutMessage) error {
	putMu.Lock()
	defer putMu.Unlock()

	endpoint := fmt.Sprintf("resource/%s/%s", resource.Type, resource.ID)
	logger.Debug("PutLight", "endpoint", endpoint, "message", msg)
	return doPut(endpoint, msg)
}
