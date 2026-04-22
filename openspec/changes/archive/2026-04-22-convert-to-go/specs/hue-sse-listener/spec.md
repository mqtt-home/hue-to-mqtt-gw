## ADDED Requirements

### Requirement: Connect to Hue SSE endpoint
The system SHALL open an HTTP GET connection to `{protocol}://{host}:{port}/eventstream/clip/v2` with the `hue-application-key` header and `Accept: text/event-stream`. The connection SHALL accept self-signed TLS certificates.

#### Scenario: Successful SSE connection
- **WHEN** the application connects to the Hue Bridge SSE endpoint
- **THEN** it SHALL receive a stream of Server-Sent Events and process each event as it arrives

### Requirement: Parse SSE events
The system SHALL parse SSE event data lines. Each event contains a JSON array of event objects, where each event object has a `type`, `id`, and `data` array of resource updates.

#### Scenario: Receive light update event
- **WHEN** an SSE event arrives with `data` containing a resource update for a light
- **THEN** the system SHALL parse the JSON and pass each resource update to the event handler

### Requirement: Automatic reconnection
The system SHALL automatically reconnect to the SSE endpoint when the connection is lost. There SHALL be a brief delay between reconnection attempts to avoid overwhelming the bridge.

#### Scenario: Connection drops
- **WHEN** the SSE connection is unexpectedly closed
- **THEN** the system SHALL log a warning and reconnect after a short delay

### Requirement: Watchdog timer
When `sse-watchdog-millis` is configured to a value greater than 0, the system SHALL monitor the SSE connection and reconnect if no events are received within the configured timeout period.

#### Scenario: Watchdog triggers reconnect
- **WHEN** `sse-watchdog-millis` is set to 30000 and no SSE events arrive for 30 seconds
- **THEN** the system SHALL close the current connection and initiate a new one

#### Scenario: Watchdog disabled
- **WHEN** `sse-watchdog-millis` is 0 or not set
- **THEN** no watchdog monitoring SHALL be active
