package messages

import (
	"time"

	"github.com/mqtt-home/hue2mqtt/hue"
)

type DevicePowerMessage struct {
	BatteryLevel int    `json:"battery_level"`
	BatteryState string `json:"battery_state"`
	LastUpdated  string `json:"last-updated"`
}

func FromDevicePower(dp hue.Resource) DevicePowerMessage {
	msg := DevicePowerMessage{
		LastUpdated: time.Now().UTC().Format(time.RFC3339Nano),
	}

	if dp.PowerState != nil {
		msg.BatteryLevel = dp.PowerState.BatteryLevel
		msg.BatteryState = dp.PowerState.BatteryState
	}

	return msg
}
