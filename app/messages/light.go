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

	if result.On == nil {
		result.On = &hue.LightOnOffData{}
	}
	result.On.On = msg.State == "ON"
	result.Dimming = &hue.LightDimmingData{Brightness: float64(msg.Brightness)}

	if msg.ColorTemp != nil && template.ColorTemperature != nil {
		ct := *template.ColorTemperature
		ct.Mirek = msg.ColorTemp
		result.ColorTemperature = &ct
		result.Color = nil
	} else if msg.Color != nil && template.Color != nil {
		result.ColorTemperature = nil
		result.Dimming = nil
		result.Color = &hue.LightColorData{XY: *msg.Color}
	}

	return result
}

func ToGroupedLight(template hue.Resource, msg LightMessage) hue.Resource {
	result := template

	if result.On == nil {
		result.On = &hue.LightOnOffData{}
	}
	result.On.On = msg.State == "ON"

	if msg.ColorTemp != nil && template.ColorTemperature != nil {
		ct := *template.ColorTemperature
		ct.Mirek = msg.ColorTemp
		result.ColorTemperature = &ct
		result.Color = nil
		result.Dimming = &hue.LightDimmingData{Brightness: float64(msg.Brightness)}
	} else if msg.Color != nil && template.Color != nil {
		result.ColorTemperature = nil
		result.Dimming = nil
		result.Color = &hue.LightColorData{XY: *msg.Color}
	}

	return result
}
