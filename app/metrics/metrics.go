package metrics

import (
	"expvar"
	"time"
)

var (
	devices              = expvar.NewMap("devices")
	mqttMessagesPublished = expvar.NewInt("mqtt_messages_published")
	sseEventsReceived    = expvar.NewInt("sse_events_received")
	sseStatus            = expvar.NewString("sse_status")
	sseLastEvent         = expvar.NewString("sse_last_event")
	startedAt            = expvar.NewString("started_at")
	lastFullUpdate       = expvar.NewString("last_full_update")
)

func SetStartedAt() {
	startedAt.Set(time.Now().UTC().Format(time.RFC3339))
}

func IncrMessagesPublished() {
	mqttMessagesPublished.Add(1)
}

func IncrSSEEventsReceived() {
	sseEventsReceived.Add(1)
}

func SetSSEStatus(status string) {
	sseStatus.Set(status)
}

func SetSSELastEvent(t time.Time) {
	sseLastEvent.Set(t.UTC().Format(time.RFC3339))
}

func SetLastFullUpdate(t time.Time) {
	lastFullUpdate.Set(t.UTC().Format(time.RFC3339))
}

func UpdateDeviceCounts(counts map[string]int64) {
	devices.Init()
	for typ, count := range counts {
		devices.Add(typ, count)
	}
}
