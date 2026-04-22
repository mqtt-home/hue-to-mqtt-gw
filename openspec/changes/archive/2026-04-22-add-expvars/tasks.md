## 1. Metrics Package

- [x] 1.1 Create `app-go/metrics/metrics.go` with expvar variables: `devices` (Map), `mqtt_messages_published` (Int), `sse_events_received` (Int), `sse_status` (String), `sse_last_event` (String), `started_at` (String), `last_full_update` (String)
- [x] 1.2 Add helper functions: `IncrMessagesPublished()`, `IncrSSEEventsReceived()`, `SetSSEStatus(status string)`, `SetSSELastEvent(t time.Time)`, `SetLastFullUpdate(t time.Time)`, `UpdateDeviceCounts(resources []hue.Resource)`
- [x] 1.3 Write unit tests for all metric helpers

## 2. Wire Metrics Into Application

- [x] 2.1 Call `metrics.SetStartedAt()` in `main.go` at startup
- [x] 2.2 Call `metrics.IncrMessagesPublished()` in `publishState()` after each successful MQTT publish
- [x] 2.3 Call `metrics.IncrSSEEventsReceived()` in the SSE event callback
- [x] 2.4 Call `metrics.SetSSEStatus("connected")` / `"disconnected"` in `hue/sse.go` on connect/disconnect
- [x] 2.5 Call `metrics.SetSSELastEvent()` when an SSE event is received
- [x] 2.6 Call `metrics.UpdateDeviceCounts()` after each `InitFromHue()` completes
- [x] 2.7 Call `metrics.SetLastFullUpdate()` after each full update completes
- [x] 2.8 Add `import _ "expvar"` in `main.go` to register the `/debug/vars` handler

## 3. Verify

- [x] 3.1 Run app and verify `curl http://localhost:6060/debug/vars` returns all custom metrics
- [x] 3.2 Run full test suite and verify all tests pass
