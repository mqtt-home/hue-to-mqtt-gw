package messages

import (
	"math"

	"github.com/mqtt-home/hue2mqtt/hue"
)

type LightMessage struct {
	State      string      `json:"state"`
	Brightness int         `json:"brightness"`
	ColorTemp  *int        `json:"color_temp,omitempty"`
	Color      *hue.ColorXY `json:"color,omitempty"`
}

type LightEffectMessage struct {
	Effect   string       `json:"effect"`
	Colors   []hue.ColorXY `json:"colors"`
	Duration int          `json:"duration"`
}

func IsEffectMessage(data map[string]interface{}) bool {
	_, ok := data["effect"]
	return ok
}

func FromLight(light hue.Resource) LightMessage {
	msg := LightMessage{
		State:      "OFF",
		Brightness: 0,
	}

	if light.On != nil && light.On.On {
		msg.State = "ON"
	}

	if light.Dimming != nil {
		msg.Brightness = int(math.Round(light.Dimming.Brightness))
	}

	if light.ColorTemperature != nil && light.ColorTemperature.Mirek != nil && light.ColorTemperature.MirekValid {
		mirek := *light.ColorTemperature.Mirek
		msg.ColorTemp = &mirek
	} else if light.Color != nil {
		xy := light.Color.XY
		msg.Color = &xy
	}

	return msg
}

func ToLight(template hue.Resource, msg LightMessage) hue.Resource {
	result := template

	// result is a shallow copy of template, so its pointer fields still alias
	// the objects held in the shared state map. Always allocate a fresh On
	// rather than writing through result.On.On, otherwise we would mutate the
	// canonical state in place from outside the state lock (data race + the
	// state would flip before the bridge confirms). Reassigning a pointer field
	// only touches our local copy, which is safe.
	result.On = &hue.LightOnOffData{On: msg.State == "ON"}
	result.Dimming = &hue.LightDimmingData{Brightness: float64(msg.Brightness)}

	switch {
	case msg.ColorTemp != nil && template.ColorTemperature != nil:
		ct := *template.ColorTemperature
		ct.Mirek = msg.ColorTemp
		result.ColorTemperature = &ct
		result.Color = nil
	case msg.Color != nil && template.Color != nil:
		result.ColorTemperature = nil
		result.Dimming = nil
		result.Color = &hue.LightColorData{XY: *msg.Color}
	default:
		// Plain on/off with no color change requested. Do not carry the
		// template's color over to the bridge: sending a color_temperature
		// with a null mirek is rejected (HTTP 400), and sending a stale color
		// makes the bulb fade to it as it powers off (it looks blue).
		result.ColorTemperature = nil
		result.Color = nil
	}

	return result
}

func ToGroupedLight(template hue.Resource, msg LightMessage) hue.Resource {
	result := template

	// Allocate a fresh On instead of mutating template.On in place; see ToLight.
	result.On = &hue.LightOnOffData{On: msg.State == "ON"}

	switch {
	case msg.ColorTemp != nil && template.ColorTemperature != nil:
		ct := *template.ColorTemperature
		ct.Mirek = msg.ColorTemp
		result.ColorTemperature = &ct
		result.Color = nil
		result.Dimming = &hue.LightDimmingData{Brightness: float64(msg.Brightness)}
	case msg.Color != nil && template.Color != nil:
		result.ColorTemperature = nil
		result.Dimming = nil
		result.Color = &hue.LightColorData{XY: *msg.Color}
	default:
		// Plain on/off with no color change requested; see ToLight.
		result.ColorTemperature = nil
		result.Color = nil
	}

	return result
}
