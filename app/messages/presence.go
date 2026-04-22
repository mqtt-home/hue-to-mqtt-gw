package messages

import (
	"time"

	"github.com/mqtt-home/hue2mqtt/hue"
)

type PresenceMessage struct {
	Presence    bool   `json:"presence"`
	LastUpdated string `json:"last-updated"`
}

func FromMotion(motion hue.Resource) PresenceMessage {
	msg := PresenceMessage{
		LastUpdated: time.Now().UTC().Format(time.RFC3339Nano),
	}

	if motion.Motion != nil {
		msg.Presence = motion.Motion.Motion
	}

	return msg
}
