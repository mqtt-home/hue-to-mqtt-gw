## ADDED Requirements

### Requirement: JSON config file loading
The system SHALL load configuration from a JSON file whose path is provided as the first command-line argument. The config file format SHALL be identical to the existing TypeScript version.

#### Scenario: Load valid config
- **WHEN** the application is started with `/path/to/config.json` as the first argument
- **THEN** it SHALL parse the JSON and populate the config struct

#### Scenario: Missing config file
- **WHEN** the config file path does not exist
- **THEN** the application SHALL log an error and exit

### Requirement: Environment variable substitution
The system SHALL replace `${VAR_NAME}` patterns in the config file with the corresponding environment variable values before parsing JSON, using the `mqtt-gateway/config.ReplaceEnvVariables` function.

#### Scenario: Env var in MQTT password
- **WHEN** the config contains `"password": "${MQTT_PASSWORD}"` and `MQTT_PASSWORD=secret123`
- **THEN** the parsed password SHALL be `secret123`

### Requirement: Default values
The system SHALL apply the following defaults when fields are not present in the config: `mqtt.qos: 1`, `mqtt.retain: true`, `mqtt.bridge-info: true`, `hue.port: 443`, `hue.protocol: "https"`, `hue.sse-watchdog-millis: 0`, `send-full-update: true`, `loglevel: "info"`.

#### Scenario: Minimal config with defaults
- **WHEN** a config file only specifies `mqtt.url`, `hue.host`, and `hue.api-key`
- **THEN** all other fields SHALL have their default values applied

### Requirement: Config struct compatibility
The config struct SHALL support the following JSON fields with kebab-case keys: `mqtt.url`, `mqtt.topic`, `mqtt.username`, `mqtt.password`, `mqtt.retain`, `mqtt.qos`, `mqtt.bridge-info`, `mqtt.bridge-info-topic`, `hue.host`, `hue.api-key`, `hue.port`, `hue.protocol`, `hue.sse-watchdog-millis`, `names` (map of UUID to string), `send-full-update`, `loglevel`.

#### Scenario: Full config with all fields
- **WHEN** a config file includes all supported fields
- **THEN** all fields SHALL be correctly parsed into the config struct

### Requirement: Logger initialization
The system SHALL initialize the `go-logger` with level "info" on startup, then update the log level after config is loaded using the configured `loglevel` value.

#### Scenario: Debug log level
- **WHEN** the config specifies `"loglevel": "debug"`
- **THEN** the logger SHALL be configured to output debug-level messages
