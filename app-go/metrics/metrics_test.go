package metrics

import (
	"testing"
	"time"
)

func TestIncrMessagesPublished(t *testing.T) {
	mqttMessagesPublished.Set(0)
	IncrMessagesPublished()
	IncrMessagesPublished()
	if mqttMessagesPublished.Value() != 2 {
		t.Errorf("expected 2, got %d", mqttMessagesPublished.Value())
	}
}

func TestIncrSSEEventsReceived(t *testing.T) {
	sseEventsReceived.Set(0)
	IncrSSEEventsReceived()
	if sseEventsReceived.Value() != 1 {
		t.Errorf("expected 1, got %d", sseEventsReceived.Value())
	}
}

func TestSetSSEStatus(t *testing.T) {
	SetSSEStatus("connected")
	if sseStatus.Value() != "connected" {
		t.Errorf("expected connected, got %s", sseStatus.Value())
	}
	SetSSEStatus("disconnected")
	if sseStatus.Value() != "disconnected" {
		t.Errorf("expected disconnected, got %s", sseStatus.Value())
	}
}

func TestSetSSELastEvent(t *testing.T) {
	ts := time.Date(2026, 4, 22, 17, 21, 0, 0, time.UTC)
	SetSSELastEvent(ts)
	if sseLastEvent.Value() != "2026-04-22T17:21:00Z" {
		t.Errorf("expected 2026-04-22T17:21:00Z, got %s", sseLastEvent.Value())
	}
}

func TestSetStartedAt(t *testing.T) {
	SetStartedAt()
	if startedAt.Value() == "" {
		t.Error("expected started_at to be set")
	}
}

func TestSetLastFullUpdate(t *testing.T) {
	ts := time.Date(2026, 4, 22, 16, 0, 0, 0, time.UTC)
	SetLastFullUpdate(ts)
	if lastFullUpdate.Value() != "2026-04-22T16:00:00Z" {
		t.Errorf("expected 2026-04-22T16:00:00Z, got %s", lastFullUpdate.Value())
	}
}

func TestUpdateDeviceCounts(t *testing.T) {
	counts := map[string]int64{
		"light":       3,
		"button":      2,
		"motion":      1,
		"temperature": 1,
	}

	UpdateDeviceCounts(counts)

	v := devices.Get("light")
	if v == nil || v.String() != "3" {
		t.Errorf("expected light=3, got %v", v)
	}
	v = devices.Get("button")
	if v == nil || v.String() != "2" {
		t.Errorf("expected button=2, got %v", v)
	}
	v = devices.Get("motion")
	if v == nil || v.String() != "1" {
		t.Errorf("expected motion=1, got %v", v)
	}
}
