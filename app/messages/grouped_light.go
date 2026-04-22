package messages

import "github.com/mqtt-home/hue2mqtt/hue"

type GroupedLightMessage struct {
	State string `json:"state"`
}

func FromGroupedLight(group hue.Resource) GroupedLightMessage {
	state := "OFF"
	if group.On != nil && group.On.On {
		state = "ON"
	}
	return GroupedLightMessage{State: state}
}
