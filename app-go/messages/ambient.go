package messages

import (
	"time"

	"github.com/mqtt-home/hue2mqtt/hue"
)

type AmbientMessage struct {
	LastLevel   int    `json:"last-level"`
	LastUpdated string `json:"last-updated"`
}

func FromLightLevel(ll hue.Resource) AmbientMessage {
	msg := AmbientMessage{
		LastUpdated: time.Now().UTC().Format(time.RFC3339Nano),
	}

	if ll.Light != nil {
		msg.LastLevel = ll.Light.LightLevel
	}

	return msg
}
