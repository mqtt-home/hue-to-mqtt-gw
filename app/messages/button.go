package messages

import (
	"time"

	"github.com/mqtt-home/hue2mqtt/hue"
)

type ButtonMessage struct {
	Button      int    `json:"button"`
	Event       string `json:"event,omitempty"`
	LastUpdated string `json:"last-updated"`
}

func FromButton(button hue.Resource) ButtonMessage {
	msg := ButtonMessage{
		LastUpdated: time.Now().UTC().Format(time.RFC3339Nano),
	}

	if button.ButtonMetadata != nil {
		msg.Button = button.ButtonMetadata.ControlID
	}

	if button.Button != nil {
		msg.Event = button.Button.LastEvent
	}

	return msg
}
