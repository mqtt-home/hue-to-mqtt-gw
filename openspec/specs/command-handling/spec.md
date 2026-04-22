## ADDED Requirements

### Requirement: Light set command
When a JSON message is received on a light's `/set` topic, the system SHALL parse it as a LightMessage and send a PUT request to the Hue Bridge to update the light state. The PUT body SHALL be constructed by merging the command with the cached light resource.

#### Scenario: Turn light on with brightness
- **WHEN** `{"state":"ON","brightness":80}` is received on `hue/light/office/desk-lamp/set`
- **THEN** the system SHALL send a PUT to `/clip/v2/resource/light/{id}` with `on.on: true` and `dimming.brightness: 80`

#### Scenario: Set color temperature
- **WHEN** `{"state":"ON","brightness":100,"color_temp":350}` is received
- **THEN** the PUT body SHALL include `color_temperature.mirek: 350` and SHALL NOT include the `color` field

#### Scenario: Set XY color
- **WHEN** `{"state":"ON","brightness":100,"color":{"x":0.3,"y":0.4}}` is received
- **THEN** the PUT body SHALL include `color.xy` and SHALL NOT include `color_temperature` or `dimming`

### Requirement: Grouped light set command
When a JSON message is received on a grouped light's `/set` topic, the system SHALL parse it and send a PUT request to update the grouped light state.

#### Scenario: Turn group on
- **WHEN** `{"state":"ON","brightness":100}` is received on a grouped light's `/set` topic
- **THEN** the system SHALL send a PUT to `/clip/v2/resource/grouped_light/{id}`

### Requirement: Light effect command
When a message with an `effect` field is received on a light's `/set` topic, the system SHALL execute the effect animation: cycling through the provided colors array with the specified duration between steps.

#### Scenario: Notify restore effect
- **WHEN** `{"effect":"notify_restore","colors":[{"x":0.7,"y":0.3},{"x":0.2,"y":0.7}],"duration":500}` is received
- **THEN** the system SHALL cycle through each color, waiting 500ms between steps, then restore the original light state

#### Scenario: Notify off effect
- **WHEN** `{"effect":"notify_off","colors":[{"x":0.7,"y":0.3}],"duration":1000}` is received
- **THEN** the system SHALL apply each color with 1000ms delay, then turn the light off

### Requirement: Get command
When any message is received on a resource's `/get` topic, the system SHALL republish the current cached state of that resource to MQTT.

#### Scenario: Request current light state
- **WHEN** any message is received on `hue/light/office/desk-lamp/get`
- **THEN** the system SHALL publish the current light state to `hue/light/office/desk-lamp`

### Requirement: Invalid command handling
When an invalid JSON message or unsupported resource type command is received, the system SHALL log an error and not crash.

#### Scenario: Malformed JSON on set topic
- **WHEN** `{invalid json` is received on a light's `/set` topic
- **THEN** the system SHALL log an error and continue processing other messages
