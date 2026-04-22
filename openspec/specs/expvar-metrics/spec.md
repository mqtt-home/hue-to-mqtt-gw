## ADDED Requirements

### Requirement: Device count metrics
The system SHALL expose an expvar map named `"devices"` containing the count of each resource type currently in the state cache.

#### Scenario: After full update
- **WHEN** a full state update completes
- **THEN** `/debug/vars` SHALL include `"devices": {"light": N, "button": N, "motion": N, ...}` reflecting actual counts

### Requirement: MQTT messages published counter
The system SHALL expose an expvar integer named `"mqtt_messages_published"` that increments each time a message is published to MQTT.

#### Scenario: Publish a message
- **WHEN** a device state message is published to MQTT
- **THEN** `"mqtt_messages_published"` SHALL increment by 1

### Requirement: SSE events received counter
The system SHALL expose an expvar integer named `"sse_events_received"` that increments for each SSE event processed.

#### Scenario: Receive SSE event
- **WHEN** an SSE event with 3 data items is received
- **THEN** `"sse_events_received"` SHALL increment by 1

### Requirement: SSE connection status
The system SHALL expose an expvar string named `"sse_status"` with the current SSE connection state ("connected" or "disconnected") and an expvar string named `"sse_last_event"` with the ISO 8601 timestamp of the last received SSE event.

#### Scenario: SSE connected and receiving
- **WHEN** the SSE connection is active and an event was received at 2026-04-22T17:21:00Z
- **THEN** `"sse_status"` SHALL be `"connected"` and `"sse_last_event"` SHALL be `"2026-04-22T17:21:00Z"`

#### Scenario: SSE disconnected
- **WHEN** the SSE connection drops
- **THEN** `"sse_status"` SHALL be `"disconnected"`

### Requirement: Uptime and last update timestamps
The system SHALL expose an expvar string named `"started_at"` with the application start time in ISO 8601, and `"last_full_update"` with the timestamp of the last completed full state refresh.

#### Scenario: Application running
- **WHEN** the app started at 2026-04-22T15:00:00Z and last full update was at 2026-04-22T16:00:00Z
- **THEN** `"started_at"` SHALL be `"2026-04-22T15:00:00Z"` and `"last_full_update"` SHALL be `"2026-04-22T16:00:00Z"`

### Requirement: Expvar endpoint availability
All expvar metrics SHALL be accessible via HTTP GET at `http://localhost:6060/debug/vars` as JSON, served by the existing pprof HTTP listener.

#### Scenario: Fetch metrics
- **WHEN** a GET request is made to `http://localhost:6060/debug/vars`
- **THEN** the response SHALL be JSON containing all registered expvar variables
