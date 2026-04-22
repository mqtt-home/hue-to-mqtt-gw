## ADDED Requirements

### Requirement: In-memory resource cache
The system SHALL maintain an in-memory map of all Hue resources keyed by their UUID. When an SSE event updates a resource, the cached resource SHALL be merged with the update (new fields overwrite, existing fields preserved).

#### Scenario: Cache populated on startup
- **WHEN** the application fetches initial resources from the Hue Bridge
- **THEN** all resources SHALL be stored in the cache indexed by their `id`

#### Scenario: Cache updated on SSE event
- **WHEN** an SSE event arrives with an update for resource ID "abc-123"
- **THEN** the cached resource with ID "abc-123" SHALL be updated by merging the event data

#### Scenario: Unknown resource in event
- **WHEN** an SSE event references a resource ID not in the cache
- **THEN** the system SHALL log a warning and skip the update

### Requirement: Topic mapping
The system SHALL map each resource to an MQTT topic based on: the resource type, the room the resource belongs to, and the resource name. Topic names SHALL be sanitized (lowercase, spaces replaced with dashes, special characters removed).

#### Scenario: Light in a room
- **WHEN** a light named "Desk Lamp" belongs to room "Office"
- **THEN** its topic SHALL be `{base-topic}/light/office/desk-lamp`

#### Scenario: Sensor not in a room
- **WHEN** a temperature sensor named "Outdoor Sensor" is not assigned to any room
- **THEN** its topic SHALL use a default location or omit the room segment

### Requirement: Custom name mapping
When the `names` configuration object maps a resource UUID to a custom name, that custom name SHALL be used instead of the Hue-provided name for topic generation.

#### Scenario: Custom name configured
- **WHEN** config has `"names": { "abc-123": "my-custom-name" }` and resource "abc-123" is a light
- **THEN** the topic SHALL use "my-custom-name" instead of the Hue device name

### Requirement: Room assignment for grouped lights
The system SHALL track which room each grouped light belongs to and use the room name as the location segment in the MQTT topic.

#### Scenario: Grouped light topic
- **WHEN** a grouped light belongs to room "Living Room"
- **THEN** its topic SHALL be `{base-topic}/grouped_light/living-room/{group-name}`
