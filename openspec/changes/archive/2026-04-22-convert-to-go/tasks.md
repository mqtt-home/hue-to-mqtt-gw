## 1. Project Scaffolding

- [x] 1.1 Create `app/` directory with `go.mod` (module `github.com/mqtt-home/hue2mqtt`, Go 1.25.0) and initial `main.go`
- [x] 1.2 Create `app/version/version.go` with Version, GitCommit, BuildTime variables
- [x] 1.3 Create `app/Makefile` with targets: build, test, coverage, lint, docker, clean, deps, run, version (following mqtt-lamarzocco pattern)
- [x] 1.4 Create `app/Dockerfile` with Go builder stage and distroless final stage

## 2. Configuration

- [x] 2.1 Create `app/config/config.go` with Config, MQTTConfig, HueConfig structs using kebab-case JSON tags
- [x] 2.2 Implement `LoadConfig()` with file reading, env var substitution via `mqtt-gateway/config.ReplaceEnvVariables`, JSON parsing, and default value application
- [x] 2.3 Write unit tests for config loading, defaults, and env var substitution

## 3. Hue API Types

- [x] 3.1 Create `app/hue/types.go` with Go structs for all Hue resource types: Light, Button, Motion, Temperature, LightLevel, DevicePower, GroupedLight, ZigbeeConnectivity, ZgpConnectivity, Device, Room
- [x] 3.2 Add resource type constants and type guard functions (e.g., `IsLight()`, `IsButton()`)
- [x] 3.3 Add Hue event types: HueEvent, HueEventData
- [x] 3.4 Write unit tests for type identification and JSON unmarshaling

## 4. Hue API Client

- [x] 4.1 Create `app/hue/api.go` with HTTP client setup (TLS skip verify, API key header)
- [x] 4.2 Implement `FetchDevices()`, `FetchRooms()`, and typed resource fetch functions (lights, buttons, motion, temperature, light_level, device_power, grouped_light, zigbee_connectivity, zgp_connectivity)
- [x] 4.3 Implement `PutLightResource()` and `PutGroupedLightResource()` with mutex serialization
- [x] 4.4 Write unit tests using httptest mock server for API responses

## 5. Topic Utilities

- [x] 5.1 Create `app/topic/utils.go` with topic sanitization (lowercase, spaces to dashes, special char removal)
- [x] 5.2 Write unit tests for topic sanitization with various inputs (unicode, spaces, special chars)

## 6. Message Converters

- [x] 6.1 Create `app/messages/light.go` with `FromLight()`, `ToLight()`, and `ToGroupedLight()` conversion functions
- [x] 6.2 Create `app/messages/button.go` with `FromButton()` conversion
- [x] 6.3 Create `app/messages/presence.go` with `FromMotion()` conversion
- [x] 6.4 Create `app/messages/temperature.go` with `FromTemperature()` conversion
- [x] 6.5 Create `app/messages/ambient.go` with `FromLightLevel()` conversion
- [x] 6.6 Create `app/messages/device_power.go` with `FromDevicePower()` conversion
- [x] 6.7 Create `app/messages/grouped_light.go` with `FromGroupedLight()` conversion
- [x] 6.8 Create `app/messages/zigbee.go` and `app/messages/zgp.go` with connectivity message converters
- [x] 6.9 Write table-driven unit tests for all message converters verifying exact JSON output

## 7. Color Types

- [x] 7.1 Create `app/colors/colors.go` with ColorXY and ColorRGB structs
- [x] 7.2 Write unit tests for color struct JSON serialization

## 8. State Management

- [x] 8.1 Create `app/state/manager.go` with resource cache (sync.RWMutex-protected map), topic mapping, room assignment, and custom name support
- [x] 8.2 Implement `InitState()` to populate cache from initial API fetch
- [x] 8.3 Implement `GetTopic()` and `GetResourceByTopic()` lookup functions
- [x] 8.4 Create `app/state/event_handler.go` with `TakeEvent()` and `PublishResource()` - routing resources to correct message converter and publishing to MQTT
- [x] 8.5 Write unit tests for state manager: cache operations, topic generation, custom names, room assignment

## 9. Command Handling

- [x] 9.1 Create `app/command/handler.go` with `PutMessage()` routing MQTT commands to Hue API PUT calls for lights and grouped lights
- [x] 9.2 Create `app/command/effects.go` with light effect animation (color cycling with duration, notify_restore and notify_off)
- [x] 9.3 Write unit tests for command parsing, light state construction, and effect message detection

## 10. SSE Client

- [x] 10.1 Create `app/hue/sse.go` with SSE client using `bufio.Scanner` over HTTP response body
- [x] 10.2 Implement automatic reconnection with delay on connection loss
- [x] 10.3 Implement configurable watchdog timer (close and reconnect if no events within timeout)
- [x] 10.4 Write unit tests for SSE parsing and watchdog logic using httptest

## 11. MQTT Integration

- [x] 11.1 Create MQTT initialization in `main.go` using `mqtt-gateway` library with client ID "hue2mqtt"
- [x] 11.2 Implement publish function wrapping `mqtt.PublishAbsolute()` with configured QoS and retain
- [x] 11.3 Implement subscribe logic for `/set` and `/get` topics, routing to command handler and state republish
- [x] 11.4 Implement bridge status publishing (online/offline) with LWT support

## 12. Application Orchestration

- [x] 12.1 Implement `main.go` entry point: logger init, config loading, pprof, signal handling, MQTT connect, initial fetch, SSE start
- [x] 12.2 Implement hourly full state refresh using `time.Ticker`
- [x] 12.3 Implement graceful shutdown on SIGTERM/SIGINT (close SSE, disconnect MQTT)

## 13. Integration and CI

- [x] 13.1 Update `.github/workflows/build.yml` for Go build, test, lint, and Docker
- [x] 13.2 Update `docker-compose.yml` for new Go-based image
- [x] 13.3 Run full test suite and verify all tests pass
- [ ] 13.4 Build Docker image and verify it starts correctly with example config
