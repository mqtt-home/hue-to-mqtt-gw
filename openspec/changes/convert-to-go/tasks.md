## 1. Project Scaffolding

- [ ] 1.1 Create `app/` directory with `go.mod` (module `github.com/mqtt-home/hue2mqtt`, Go 1.25.0) and initial `main.go`
- [ ] 1.2 Create `app/version/version.go` with Version, GitCommit, BuildTime variables
- [ ] 1.3 Create `app/Makefile` with targets: build, test, coverage, lint, docker, clean, deps, run, version (following mqtt-lamarzocco pattern)
- [ ] 1.4 Create `app/Dockerfile` with Go builder stage and distroless final stage

## 2. Configuration

- [ ] 2.1 Create `app/config/config.go` with Config, MQTTConfig, HueConfig structs using kebab-case JSON tags
- [ ] 2.2 Implement `LoadConfig()` with file reading, env var substitution via `mqtt-gateway/config.ReplaceEnvVariables`, JSON parsing, and default value application
- [ ] 2.3 Write unit tests for config loading, defaults, and env var substitution

## 3. Hue API Types

- [ ] 3.1 Create `app/hue/types.go` with Go structs for all Hue resource types: Light, Button, Motion, Temperature, LightLevel, DevicePower, GroupedLight, ZigbeeConnectivity, ZgpConnectivity, Device, Room
- [ ] 3.2 Add resource type constants and type guard functions (e.g., `IsLight()`, `IsButton()`)
- [ ] 3.3 Add Hue event types: HueEvent, HueEventData
- [ ] 3.4 Write unit tests for type identification and JSON unmarshaling

## 4. Hue API Client

- [ ] 4.1 Create `app/hue/api.go` with HTTP client setup (TLS skip verify, API key header)
- [ ] 4.2 Implement `FetchDevices()`, `FetchRooms()`, and typed resource fetch functions (lights, buttons, motion, temperature, light_level, device_power, grouped_light, zigbee_connectivity, zgp_connectivity)
- [ ] 4.3 Implement `PutLightResource()` and `PutGroupedLightResource()` with mutex serialization
- [ ] 4.4 Write unit tests using httptest mock server for API responses

## 5. Topic Utilities

- [ ] 5.1 Create `app/topic/utils.go` with topic sanitization (lowercase, spaces to dashes, special char removal)
- [ ] 5.2 Write unit tests for topic sanitization with various inputs (unicode, spaces, special chars)

## 6. Message Converters

- [ ] 6.1 Create `app/messages/light.go` with `FromLight()`, `ToLight()`, and `ToGroupedLight()` conversion functions
- [ ] 6.2 Create `app/messages/button.go` with `FromButton()` conversion
- [ ] 6.3 Create `app/messages/presence.go` with `FromMotion()` conversion
- [ ] 6.4 Create `app/messages/temperature.go` with `FromTemperature()` conversion
- [ ] 6.5 Create `app/messages/ambient.go` with `FromLightLevel()` conversion
- [ ] 6.6 Create `app/messages/device_power.go` with `FromDevicePower()` conversion
- [ ] 6.7 Create `app/messages/grouped_light.go` with `FromGroupedLight()` conversion
- [ ] 6.8 Create `app/messages/zigbee.go` and `app/messages/zgp.go` with connectivity message converters
- [ ] 6.9 Write table-driven unit tests for all message converters verifying exact JSON output

## 7. Color Types

- [ ] 7.1 Create `app/colors/colors.go` with ColorXY and ColorRGB structs
- [ ] 7.2 Write unit tests for color struct JSON serialization

## 8. State Management

- [ ] 8.1 Create `app/state/manager.go` with resource cache (sync.RWMutex-protected map), topic mapping, room assignment, and custom name support
- [ ] 8.2 Implement `InitState()` to populate cache from initial API fetch
- [ ] 8.3 Implement `GetTopic()` and `GetResourceByTopic()` lookup functions
- [ ] 8.4 Create `app/state/event_handler.go` with `TakeEvent()` and `PublishResource()` - routing resources to correct message converter and publishing to MQTT
- [ ] 8.5 Write unit tests for state manager: cache operations, topic generation, custom names, room assignment

## 9. Command Handling

- [ ] 9.1 Create `app/command/handler.go` with `PutMessage()` routing MQTT commands to Hue API PUT calls for lights and grouped lights
- [ ] 9.2 Create `app/command/effects.go` with light effect animation (color cycling with duration, notify_restore and notify_off)
- [ ] 9.3 Write unit tests for command parsing, light state construction, and effect message detection

## 10. SSE Client

- [ ] 10.1 Create `app/hue/sse.go` with SSE client using `bufio.Scanner` over HTTP response body
- [ ] 10.2 Implement automatic reconnection with delay on connection loss
- [ ] 10.3 Implement configurable watchdog timer (close and reconnect if no events within timeout)
- [ ] 10.4 Write unit tests for SSE parsing and watchdog logic using httptest

## 11. MQTT Integration

- [ ] 11.1 Create MQTT initialization in `main.go` using `mqtt-gateway` library with client ID "hue2mqtt"
- [ ] 11.2 Implement publish function wrapping `mqtt.PublishAbsolute()` with configured QoS and retain
- [ ] 11.3 Implement subscribe logic for `/set` and `/get` topics, routing to command handler and state republish
- [ ] 11.4 Implement bridge status publishing (online/offline) with LWT support

## 12. Application Orchestration

- [ ] 12.1 Implement `main.go` entry point: logger init, config loading, pprof, signal handling, MQTT connect, initial fetch, SSE start
- [ ] 12.2 Implement hourly full state refresh using `time.Ticker`
- [ ] 12.3 Implement graceful shutdown on SIGTERM/SIGINT (close SSE, disconnect MQTT)

## 13. Integration and CI

- [ ] 13.1 Update `.github/workflows/build.yml` for Go build, test, lint, and Docker
- [ ] 13.2 Update `docker-compose.yml` for new Go-based image
- [ ] 13.3 Run full test suite and verify all tests pass
- [ ] 13.4 Build Docker image and verify it starts correctly with example config
