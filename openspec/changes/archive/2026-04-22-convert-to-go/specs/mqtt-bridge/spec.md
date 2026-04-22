## ADDED Requirements

### Requirement: Connect to MQTT broker
The system SHALL connect to the MQTT broker using the `mqtt-gateway` library with the configured URL, username, password, QoS, and retain settings. The client ID SHALL be `hue2mqtt`.

#### Scenario: Successful MQTT connection
- **WHEN** the application starts with valid MQTT configuration
- **THEN** it SHALL establish a connection to the MQTT broker

### Requirement: Publish device state to MQTT
The system SHALL publish device state messages as JSON to MQTT topics following the pattern `{base-topic}/{resource-type}/{location}/{device-name}`. Messages SHALL be published with the configured QoS and retain settings.

#### Scenario: Publish light state
- **WHEN** a light resource update is received
- **THEN** the system SHALL publish a JSON message to `{base-topic}/light/{room}/{light-name}` with state, brightness, color_temp, and color fields

### Requirement: Subscribe to control commands
The system SHALL subscribe to `{topic}/set` and `{topic}/get` for each controllable resource (lights and grouped lights). When a message arrives on a `/set` topic, it SHALL be routed to the command handler. When a message arrives on a `/get` topic, the current state SHALL be republished.

#### Scenario: Receive set command
- **WHEN** a JSON message is published to `{base-topic}/light/{room}/{light-name}/set`
- **THEN** the system SHALL parse the message and send the corresponding PUT request to the Hue Bridge

#### Scenario: Receive get command
- **WHEN** any message is published to `{base-topic}/light/{room}/{light-name}/get`
- **THEN** the system SHALL republish the current state of that resource

### Requirement: Bridge status publishing
When `bridge-info` is enabled (default: true), the system SHALL publish "online" to the bridge info topic on successful startup and configure a Last Will and Testament (LWT) message of "offline" for the same topic.

#### Scenario: Bridge comes online
- **WHEN** the application successfully connects to both Hue Bridge and MQTT broker
- **THEN** it SHALL publish "online" to `{base-topic}/bridge/state` (or configured `bridge-info-topic`)

#### Scenario: Bridge goes offline unexpectedly
- **WHEN** the MQTT connection is lost unexpectedly
- **THEN** the broker SHALL publish "offline" to the bridge info topic via LWT

### Requirement: Full state update on startup
When `send-full-update` is true (default), the system SHALL publish the current state of all resources to MQTT after initial connection and device fetch.

#### Scenario: Initial state publish
- **WHEN** the application starts with `send-full-update: true`
- **THEN** it SHALL fetch all resources and publish each one's state to MQTT

### Requirement: Hourly full state refresh
The system SHALL perform a full state refresh every hour, fetching all resources from the Hue Bridge and republishing their state to MQTT.

#### Scenario: Hourly refresh
- **WHEN** one hour has elapsed since the last full refresh
- **THEN** the system SHALL fetch all resources and republish their state
