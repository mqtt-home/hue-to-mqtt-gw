package hue

import "encoding/json"

// Resource represents a reference to a Hue resource
type ResourceRef struct {
	RID   string `json:"rid"`
	RType string `json:"rtype"`
}

// Metadata contains name and archetype
type Metadata struct {
	Archetype string `json:"archetype,omitempty"`
	Name      string `json:"name"`
}

// ButtonMetadata contains control_id for buttons
type ButtonMetadata struct {
	ControlID int `json:"control_id"`
}

// ColorXY represents a CIE XY color
type ColorXY struct {
	X float64 `json:"x"`
	Y float64 `json:"y"`
}

// Gamut defines the color gamut triangle
type Gamut struct {
	Red   ColorXY `json:"red"`
	Green ColorXY `json:"green"`
	Blue  ColorXY `json:"blue"`
}

// LightOnOffData holds on/off state
type LightOnOffData struct {
	On bool `json:"on"`
}

// LightDimmingData holds brightness
type LightDimmingData struct {
	Brightness  float64  `json:"brightness"`
	MinDimLevel *float64 `json:"min_dim_level,omitempty"`
}

// MirekSchema holds mirek range
type MirekSchema struct {
	MirekMaximum int `json:"mirek_maximum"`
	MirekMinimum int `json:"mirek_minimum"`
}

// LightColorTemperatureData holds color temperature info
type LightColorTemperatureData struct {
	Mirek       *int         `json:"mirek"`
	MirekValid  bool         `json:"mirek_valid"`
	MirekSchema *MirekSchema `json:"mirek_schema,omitempty"`
}

// LightColorData holds color info
type LightColorData struct {
	XY        ColorXY  `json:"xy"`
	Gamut     *Gamut   `json:"gamut,omitempty"`
	GamutType *string  `json:"gamut_type,omitempty"`
}

// Resource is the base interface for all Hue resources. We use a generic
// struct with json.RawMessage fields and type-specific accessors.
type Resource struct {
	Type  string `json:"type,omitempty"`
	ID    string `json:"id"`
	IDV1  string `json:"id_v1,omitempty"`
	Owner *ResourceRef `json:"owner,omitempty"`

	// Nameable fields
	Metadata *Metadata `json:"metadata,omitempty"`

	// Light fields
	On               *LightOnOffData            `json:"on,omitempty"`
	Dimming          *LightDimmingData           `json:"dimming,omitempty"`
	ColorTemperature *LightColorTemperatureData  `json:"color_temperature,omitempty"`
	Color            *LightColorData             `json:"color,omitempty"`
	Effects          json.RawMessage             `json:"effects,omitempty"`
	Dynamics         json.RawMessage             `json:"dynamics,omitempty"`
	Alert            json.RawMessage             `json:"alert,omitempty"`
	Mode             string                      `json:"mode,omitempty"`
	Gradient         json.RawMessage             `json:"gradient,omitempty"`

	// Button fields
	ButtonMetadata *ButtonMetadata `json:"-"`
	Button         *ButtonData    `json:"button,omitempty"`

	// Motion fields
	Enabled *bool       `json:"enabled,omitempty"`
	Motion  *MotionData `json:"motion,omitempty"`

	// Temperature fields
	Temperature *TemperatureData `json:"temperature,omitempty"`

	// Light level fields
	Light *LightLevelData `json:"light,omitempty"`

	// Device power fields
	PowerState *DevicePowerData `json:"power_state,omitempty"`

	// Zigbee connectivity fields
	Status     json.RawMessage `json:"status,omitempty"`
	MacAddress string          `json:"mac_address,omitempty"`

	// ZGP connectivity fields
	SourceID string `json:"source_id,omitempty"`

	// Device fields
	Services    []ResourceRef `json:"services,omitempty"`
	ProductData json.RawMessage `json:"product_data,omitempty"`

	// Room fields
	Children []ResourceRef `json:"children,omitempty"`
}

// Custom UnmarshalJSON to handle button metadata vs regular metadata
func (r *Resource) UnmarshalJSON(data []byte) error {
	// Use an alias to avoid infinite recursion
	type Alias Resource
	aux := &struct {
		Metadata json.RawMessage `json:"metadata,omitempty"`
		*Alias
	}{
		Alias: (*Alias)(r),
	}

	if err := json.Unmarshal(data, aux); err != nil {
		return err
	}

	if aux.Metadata != nil {
		// Try button metadata (has control_id)
		var btnMeta ButtonMetadata
		if err := json.Unmarshal(aux.Metadata, &btnMeta); err == nil && r.Type == "button" {
			r.ButtonMetadata = &btnMeta
		}

		// Try regular metadata (has name)
		var meta Metadata
		if err := json.Unmarshal(aux.Metadata, &meta); err == nil && meta.Name != "" {
			r.Metadata = &meta
		}
	}

	return nil
}

// ButtonData holds button event data
type ButtonData struct {
	LastEvent string `json:"last_event,omitempty"`
}

// MotionData holds motion sensor data
type MotionData struct {
	Motion      bool `json:"motion"`
	MotionValid bool `json:"motion_valid"`
}

// TemperatureData holds temperature sensor data
type TemperatureData struct {
	Temperature      float64 `json:"temperature"`
	TemperatureValid bool    `json:"temperature_valid"`
}

// LightLevelData holds ambient light sensor data
type LightLevelData struct {
	LightLevel      int  `json:"light_level"`
	LightLevelValid bool `json:"light_level_valid"`
}

// DevicePowerData holds battery state
type DevicePowerData struct {
	BatteryState string `json:"battery_state"`
	BatteryLevel int    `json:"battery_level"`
}

// HueEvent represents an SSE event from the Hue Bridge
type HueEvent struct {
	CreationTime string         `json:"creationtime"`
	Data         []HueEventData `json:"data"`
	ID           string         `json:"id"`
	Type         string         `json:"type"`
}

// HueEventData represents a single resource update in an event
type HueEventData struct {
	Type  string `json:"type,omitempty"`
	ID    string `json:"id"`
	IDV1  string `json:"id_v1,omitempty"`
	Owner *ResourceRef `json:"owner,omitempty"`

	// Possible update fields
	On               *LightOnOffData            `json:"on,omitempty"`
	Dimming          *LightDimmingData           `json:"dimming,omitempty"`
	ColorTemperature *LightColorTemperatureData  `json:"color_temperature,omitempty"`
	Color            *LightColorData             `json:"color,omitempty"`
	Motion           *MotionData                 `json:"motion,omitempty"`
	Button           *ButtonData                 `json:"button,omitempty"`
	Temperature      *TemperatureData            `json:"temperature,omitempty"`
	Light            *LightLevelData             `json:"light,omitempty"`
	PowerState       *DevicePowerData            `json:"power_state,omitempty"`
	Status           json.RawMessage             `json:"status,omitempty"`
	Enabled          *bool                       `json:"enabled,omitempty"`
}

// Result wraps Hue API responses
type Result struct {
	Errors []json.RawMessage `json:"errors"`
	Data   []Resource        `json:"data"`
}

// Type check functions

func IsLight(r Resource) bool {
	return r.Type == "light"
}

func IsButton(r Resource) bool {
	return r.Type == "button"
}

func IsMotion(r Resource) bool {
	return r.Type == "motion"
}

func IsTemperature(r Resource) bool {
	return r.Type == "temperature"
}

func IsLightLevel(r Resource) bool {
	return r.Type == "light_level"
}

func IsDevicePower(r Resource) bool {
	return r.Type == "device_power"
}

func IsGroupedLight(r Resource) bool {
	return r.Type == "grouped_light"
}

func IsZigbeeConnectivity(r Resource) bool {
	return r.Type == "zigbee_connectivity"
}

func IsZgpConnectivity(r Resource) bool {
	return r.Type == "zgp_connectivity"
}

func IsRoom(r Resource) bool {
	return r.Type == "room"
}

func IsTrigger(r Resource) bool {
	return IsButton(r) || IsMotion(r)
}

func IsNameable(r Resource) bool {
	return r.Metadata != nil && r.Metadata.Name != ""
}

// StatusString extracts the status as a string. The status field can be
// either a JSON string (for zigbee/zgp connectivity) or an object (for other types).
func (r Resource) StatusString() string {
	if len(r.Status) == 0 {
		return ""
	}
	var s string
	if err := json.Unmarshal(r.Status, &s); err != nil {
		return ""
	}
	return s
}
