package messages

import (
	"time"

	"github.com/mqtt-home/hue2mqtt/hue"
)

type ZigbeeConnectivityMessage struct {
	Status      string `json:"status"`
	LastUpdated string `json:"last-updated"`
}

func FromZigbeeConnectivity(zc hue.Resource) ZigbeeConnectivityMessage {
	return ZigbeeConnectivityMessage{
		Status:      zc.StatusString(),
		LastUpdated: time.Now().UTC().Format(time.RFC3339Nano),
	}
}
