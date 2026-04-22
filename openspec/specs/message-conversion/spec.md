## ADDED Requirements

### Requirement: Light message format
The system SHALL convert a Hue light resource to a JSON message with the following fields: `state` ("ON" or "OFF"), `brightness` (integer, rounded from float), `color_temp` (mirek value, only if mirek_valid is true), `color` (XY object, only if no valid color_temp).

#### Scenario: Light is on with color temperature
- **WHEN** a light resource has `on.on: true`, `dimming.brightness: 75.5`, `color_temperature.mirek: 350`, `color_temperature.mirek_valid: true`
- **THEN** the message SHALL be `{"state":"ON","brightness":76,"color_temp":350}`

#### Scenario: Light is off with XY color
- **WHEN** a light resource has `on.on: false`, `dimming.brightness: 0`, `color_temperature.mirek_valid: false`, `color.xy: {x: 0.3, y: 0.4}`
- **THEN** the message SHALL be `{"state":"OFF","brightness":0,"color":{"x":0.3,"y":0.4}}`

### Requirement: Button message format
The system SHALL convert a Hue button resource to a JSON message with fields: `button` (control_id integer), `event` (last_event string), `last-updated` (ISO 8601 timestamp).

#### Scenario: Button press event
- **WHEN** a button resource has `metadata.control_id: 1`, `button.last_event: "short_release"`
- **THEN** the message SHALL be `{"button":1,"event":"short_release","last-updated":"<ISO-timestamp>"}`

### Requirement: Presence message format
The system SHALL convert a Hue motion resource to a JSON message with fields: `presence` (boolean), `last-updated` (ISO 8601 timestamp).

#### Scenario: Motion detected
- **WHEN** a motion resource has `motion.motion: true`
- **THEN** the message SHALL be `{"presence":true,"last-updated":"<ISO-timestamp>"}`

### Requirement: Temperature message format
The system SHALL convert a Hue temperature resource to a JSON message with fields: `temperature` (float, rounded to 2 decimal places), `last-updated` (ISO 8601 timestamp).

#### Scenario: Temperature reading
- **WHEN** a temperature resource has `temperature.temperature: 21.456`
- **THEN** the message SHALL be `{"temperature":21.46,"last-updated":"<ISO-timestamp>"}`

### Requirement: Ambient light level message format
The system SHALL convert a Hue light_level resource to a JSON message with fields: `light_level` (integer), `last-updated` (ISO 8601 timestamp).

#### Scenario: Light level reading
- **WHEN** a light_level resource has `light.light_level: 12000`
- **THEN** the message SHALL be `{"light_level":12000,"last-updated":"<ISO-timestamp>"}`

### Requirement: Device power message format
The system SHALL convert a Hue device_power resource to a JSON message with fields: `battery_level` (integer), `battery_state` (string), `last-updated` (ISO 8601 timestamp).

#### Scenario: Battery status
- **WHEN** a device_power resource has `power_state.battery_level: 85`, `power_state.battery_state: "normal"`
- **THEN** the message SHALL be `{"battery_level":85,"battery_state":"normal","last-updated":"<ISO-timestamp>"}`

### Requirement: Grouped light message format
The system SHALL convert a Hue grouped_light resource to a JSON message with fields: `state` ("ON" or "OFF").

#### Scenario: Group is on
- **WHEN** a grouped_light resource has `on.on: true`
- **THEN** the message SHALL be `{"state":"ON"}`

### Requirement: Zigbee connectivity message format
The system SHALL convert zigbee_connectivity and zgp_connectivity resources to JSON messages with fields: `status` (string), `last-updated` (ISO 8601 timestamp).

#### Scenario: Zigbee connected
- **WHEN** a zigbee_connectivity resource has `status: "connected"`
- **THEN** the message SHALL be `{"status":"connected","last-updated":"<ISO-timestamp>"}`

### Requirement: Unhandled resource types
When a resource type is not recognized by any message converter, the system SHALL publish the raw resource data to `unhandled/{original-topic}`.

#### Scenario: Unknown resource type
- **WHEN** an SSE event contains a resource of an unrecognized type
- **THEN** the system SHALL publish the raw JSON to `unhandled/{topic}`
