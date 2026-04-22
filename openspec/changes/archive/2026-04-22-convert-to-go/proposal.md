## Why

The hue-to-mqtt gateway is currently written in TypeScript/Node.js, which carries significant memory overhead for a long-running IoT bridge service. Converting to Go will dramatically reduce memory usage, produce a single static binary, and align with the existing Go-based smart home infrastructure (e.g., mqtt-lamarzocco). This also eliminates the Node.js runtime dependency and simplifies deployment.

## What Changes

- **BREAKING**: Complete rewrite of the application from TypeScript to Go
- Replace all Node.js dependencies with Go equivalents (paho.mqtt, net/http, SSE client)
- Adopt the same project patterns as mqtt-lamarzocco: `go-logger`, `mqtt-gateway`, pprof, JSON config with env var substitution
- Add a comprehensive Makefile for building, running, and Docker image creation
- Add a multi-stage Dockerfile producing a distroless container image
- Implement comprehensive unit tests for all core logic
- Maintain 100% MQTT topic and message format compatibility with the existing TypeScript version

## Capabilities

### New Capabilities
- `hue-api-client`: HTTP client for Hue API v2 - fetching devices, rooms, resources, and sending PUT commands
- `hue-sse-listener`: Server-Sent Events client for real-time Hue Bridge event streaming with reconnection and watchdog
- `mqtt-bridge`: MQTT client integration for publishing device state and subscribing to control commands
- `state-management`: In-memory resource cache, topic mapping, and room assignment logic
- `message-conversion`: Converting Hue resource types (lights, buttons, motion, temperature, ambient, device-power, grouped-light, zigbee/zgp connectivity) to MQTT message formats
- `command-handling`: Processing MQTT set/get commands and translating them to Hue API PUT requests, including light effects
- `color-conversion`: XY and RGB color space conversion utilities
- `configuration`: JSON config loading with environment variable substitution and sensible defaults
- `build-and-deploy`: Makefile, multi-stage Dockerfile, and CI/CD pipeline for Go builds

### Modified Capabilities

(none - this is a full rewrite, no existing specs to modify)

## Impact

- **Code**: Entire `app/` directory (TypeScript source) will be replaced by Go source code
- **Dependencies**: Node.js/pnpm ecosystem replaced with Go modules (go-logger, mqtt-gateway, paho.mqtt, etc.)
- **Docker**: New multi-stage Dockerfile with distroless base image instead of node:alpine
- **CI/CD**: GitHub Actions workflows updated for Go build/test/lint
- **Config**: Same JSON config format maintained for backward compatibility - existing config files work without changes
- **MQTT API**: No changes - all topics, message formats, QoS, and retain behavior preserved
