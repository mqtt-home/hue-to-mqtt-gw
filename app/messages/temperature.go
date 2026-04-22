package messages

import (
	"math"
	"time"

	"github.com/mqtt-home/hue2mqtt/hue"
)

type TemperatureMessage struct {
	Temperature float64 `json:"temperature"`
	LastUpdated string  `json:"last-updated"`
}

func FromTemperature(temp hue.Resource) TemperatureMessage {
	msg := TemperatureMessage{
		LastUpdated: time.Now().UTC().Format(time.RFC3339Nano),
	}

	if temp.Temperature != nil {
		msg.Temperature = math.Round(temp.Temperature.Temperature*100) / 100
	}

	return msg
}
