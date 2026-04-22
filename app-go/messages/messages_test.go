package messages

import (
	"encoding/json"
	"strings"
	"testing"

	"github.com/mqtt-home/hue2mqtt/hue"
)

func intPtr(i int) *int { return &i }

func TestFromLight_OnWithColorTemp(t *testing.T) {
	light := hue.Resource{
		Type:    "light",
		On:      &hue.LightOnOffData{On: true},
		Dimming: &hue.LightDimmingData{Brightness: 75.5},
		ColorTemperature: &hue.LightColorTemperatureData{
			Mirek:      intPtr(350),
			MirekValid: true,
		},
	}

	msg := FromLight(light)
	if msg.State != "ON" {
		t.Errorf("expected ON, got %s", msg.State)
	}
	if msg.Brightness != 76 {
		t.Errorf("expected 76, got %d", msg.Brightness)
	}
	if msg.ColorTemp == nil || *msg.ColorTemp != 350 {
		t.Errorf("expected color_temp 350, got %v", msg.ColorTemp)
	}
	if msg.Color != nil {
		t.Error("expected no color")
	}
}

func TestFromLight_OffWithXYColor(t *testing.T) {
	light := hue.Resource{
		Type:    "light",
		On:      &hue.LightOnOffData{On: false},
		Dimming: &hue.LightDimmingData{Brightness: 0},
		ColorTemperature: &hue.LightColorTemperatureData{
			MirekValid: false,
		},
		Color: &hue.LightColorData{XY: hue.ColorXY{X: 0.3, Y: 0.4}},
	}

	msg := FromLight(light)
	if msg.State != "OFF" {
		t.Errorf("expected OFF, got %s", msg.State)
	}
	if msg.Brightness != 0 {
		t.Errorf("expected 0, got %d", msg.Brightness)
	}
	if msg.ColorTemp != nil {
		t.Error("expected no color_temp")
	}
	if msg.Color == nil || msg.Color.X != 0.3 || msg.Color.Y != 0.4 {
		t.Errorf("expected color {0.3, 0.4}, got %v", msg.Color)
	}
}

func TestFromLight_JSON(t *testing.T) {
	light := hue.Resource{
		Type:    "light",
		On:      &hue.LightOnOffData{On: true},
		Dimming: &hue.LightDimmingData{Brightness: 76},
		ColorTemperature: &hue.LightColorTemperatureData{
			Mirek:      intPtr(350),
			MirekValid: true,
		},
	}

	msg := FromLight(light)
	data, _ := json.Marshal(msg)
	s := string(data)

	if !strings.Contains(s, `"state":"ON"`) {
		t.Errorf("expected state ON in JSON: %s", s)
	}
	if !strings.Contains(s, `"brightness":76`) {
		t.Errorf("expected brightness 76 in JSON: %s", s)
	}
	if !strings.Contains(s, `"color_temp":350`) {
		t.Errorf("expected color_temp 350 in JSON: %s", s)
	}
	if strings.Contains(s, `"color"`) {
		t.Errorf("expected no color in JSON: %s", s)
	}
}

func TestToLight_OnWithColorTemp(t *testing.T) {
	template := hue.Resource{
		Type: "light",
		On:   &hue.LightOnOffData{On: false},
		ColorTemperature: &hue.LightColorTemperatureData{
			Mirek:      intPtr(250),
			MirekValid: true,
		},
	}
	ct := 350
	msg := LightMessage{State: "ON", Brightness: 80, ColorTemp: &ct}
	result := ToLight(template, msg)

	if !result.On.On {
		t.Error("expected on=true")
	}
	if result.Dimming == nil || result.Dimming.Brightness != 80 {
		t.Errorf("expected brightness 80, got %v", result.Dimming)
	}
	if result.ColorTemperature == nil || result.ColorTemperature.Mirek == nil || *result.ColorTemperature.Mirek != 350 {
		t.Error("expected mirek 350")
	}
	if result.Color != nil {
		t.Error("expected no color")
	}
}

func TestToLight_OnWithXYColor(t *testing.T) {
	template := hue.Resource{
		Type:  "light",
		On:    &hue.LightOnOffData{On: false},
		Color: &hue.LightColorData{XY: hue.ColorXY{X: 0.1, Y: 0.1}},
	}
	xy := hue.ColorXY{X: 0.3, Y: 0.4}
	msg := LightMessage{State: "ON", Brightness: 100, Color: &xy}
	result := ToLight(template, msg)

	if result.ColorTemperature != nil {
		t.Error("expected no color_temperature")
	}
	if result.Dimming != nil {
		t.Error("expected no dimming when color set")
	}
	if result.Color == nil || result.Color.XY.X != 0.3 || result.Color.XY.Y != 0.4 {
		t.Errorf("expected color {0.3, 0.4}, got %v", result.Color)
	}
}

func TestFromButton(t *testing.T) {
	btn := hue.Resource{
		Type:           "button",
		ButtonMetadata: &hue.ButtonMetadata{ControlID: 1},
		Button:         &hue.ButtonData{LastEvent: "short_release"},
	}

	msg := FromButton(btn)
	if msg.Button != 1 {
		t.Errorf("expected button 1, got %d", msg.Button)
	}
	if msg.Event != "short_release" {
		t.Errorf("expected event short_release, got %s", msg.Event)
	}
	if msg.LastUpdated == "" {
		t.Error("expected last-updated to be set")
	}
}

func TestFromMotion(t *testing.T) {
	motion := hue.Resource{
		Type:   "motion",
		Motion: &hue.MotionData{Motion: true, MotionValid: true},
	}

	msg := FromMotion(motion)
	if !msg.Presence {
		t.Error("expected presence=true")
	}
	if msg.LastUpdated == "" {
		t.Error("expected last-updated")
	}
}

func TestFromTemperature(t *testing.T) {
	temp := hue.Resource{
		Type:        "temperature",
		Temperature: &hue.TemperatureData{Temperature: 21.456},
	}

	msg := FromTemperature(temp)
	if msg.Temperature != 21.46 {
		t.Errorf("expected 21.46, got %v", msg.Temperature)
	}
}

func TestFromLightLevel(t *testing.T) {
	ll := hue.Resource{
		Type:  "light_level",
		Light: &hue.LightLevelData{LightLevel: 12000},
	}

	msg := FromLightLevel(ll)
	if msg.LastLevel != 12000 {
		t.Errorf("expected 12000, got %d", msg.LastLevel)
	}
}

func TestFromDevicePower(t *testing.T) {
	dp := hue.Resource{
		Type:       "device_power",
		PowerState: &hue.DevicePowerData{BatteryLevel: 85, BatteryState: "normal"},
	}

	msg := FromDevicePower(dp)
	if msg.BatteryLevel != 85 {
		t.Errorf("expected 85, got %d", msg.BatteryLevel)
	}
	if msg.BatteryState != "normal" {
		t.Errorf("expected normal, got %s", msg.BatteryState)
	}
}

func TestFromGroupedLight(t *testing.T) {
	tests := []struct {
		name string
		on   bool
		want string
	}{
		{"on", true, "ON"},
		{"off", false, "OFF"},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			group := hue.Resource{
				Type: "grouped_light",
				On:   &hue.LightOnOffData{On: tt.on},
			}
			msg := FromGroupedLight(group)
			if msg.State != tt.want {
				t.Errorf("expected %s, got %s", tt.want, msg.State)
			}
		})
	}
}

func TestFromZigbeeConnectivity(t *testing.T) {
	zc := hue.Resource{
		Type:   "zigbee_connectivity",
		Status: []byte(`"connected"`),
	}
	msg := FromZigbeeConnectivity(zc)
	if msg.Status != "connected" {
		t.Errorf("expected connected, got %s", msg.Status)
	}
}

func TestFromZgpConnectivity(t *testing.T) {
	zgp := hue.Resource{
		Type:   "zgp_connectivity",
		Status: []byte(`"disconnected"`),
	}
	msg := FromZgpConnectivity(zgp)
	if msg.Status != "disconnected" {
		t.Errorf("expected disconnected, got %s", msg.Status)
	}
}

func TestIsEffectMessage(t *testing.T) {
	effect := map[string]any{"effect": "notify_restore", "colors": []any{}, "duration": 500}
	if !IsEffectMessage(effect) {
		t.Error("expected true for effect message")
	}

	normal := map[string]any{"state": "ON", "brightness": 100}
	if IsEffectMessage(normal) {
		t.Error("expected false for normal message")
	}
}

func TestToGroupedLight(t *testing.T) {
	template := hue.Resource{
		Type: "grouped_light",
		On:   &hue.LightOnOffData{On: false},
		ColorTemperature: &hue.LightColorTemperatureData{
			Mirek:      intPtr(300),
			MirekValid: true,
		},
	}
	ct := 400
	msg := LightMessage{State: "ON", Brightness: 90, ColorTemp: &ct}
	result := ToGroupedLight(template, msg)

	if !result.On.On {
		t.Error("expected on=true")
	}
	if result.ColorTemperature == nil || *result.ColorTemperature.Mirek != 400 {
		t.Error("expected mirek 400")
	}
	if result.Dimming == nil || result.Dimming.Brightness != 90 {
		t.Errorf("expected brightness 90, got %v", result.Dimming)
	}
}
