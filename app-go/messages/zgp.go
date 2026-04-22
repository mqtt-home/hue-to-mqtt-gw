package messages

import (
	"time"

	"github.com/mqtt-home/hue2mqtt/hue"
)

type ZgpConnectivityMessage struct {
	Status      string `json:"status"`
	LastUpdated string `json:"last-updated"`
}

func FromZgpConnectivity(zgp hue.Resource) ZgpConnectivityMessage {
	return ZgpConnectivityMessage{
		Status:      zgp.StatusString(),
		LastUpdated: time.Now().UTC().Format(time.RFC3339Nano),
	}
}
