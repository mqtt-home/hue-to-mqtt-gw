## Context

The hue-to-mqtt gateway is a TypeScript/Node.js application that bridges Philips Hue devices to MQTT. It uses the Hue API v2 with SSE for real-time updates, publishes device state to MQTT, and accepts commands via MQTT to control lights. The application runs as a long-lived Docker container.

The user maintains a Go-based smart home project (mqtt-lamarzocco) that establishes conventions for logging, configuration, MQTT integration, build tooling, and Docker packaging. This rewrite SHALL follow those conventions exactly.

## Goals / Non-Goals

**Goals:**
- Rewrite the entire application in Go, producing a single static binary
- Reduce memory footprint compared to Node.js runtime
- Maintain 100% MQTT API compatibility (topics, message formats, QoS, retain)
- Maintain 100% config file compatibility (existing JSON configs work unchanged)
- Follow mqtt-lamarzocco patterns: go-logger, mqtt-gateway, pprof, Makefile, distroless Docker
- Achieve comprehensive unit test coverage for all message conversion, config, state, and command logic
- Support graceful shutdown via OS signals

**Non-Goals:**
- Web UI (the current TypeScript version has none, mqtt-lamarzocco has one but it's not needed here)
- Changing the MQTT topic structure or message formats
- Changing the config file format
- Supporting Hue API v1
- Adding new features beyond what the TypeScript version provides

## Decisions

### 1. Go module name: `github.com/mqtt-home/hue2mqtt`

**Rationale:** Follows the mqtt-home GitHub organization pattern used by mqtt-lamarzocco. Short, descriptive name.

### 2. Package structure

```
app/
├── main.go                  # Entry point, signal handling, pprof
├── go.mod
├── Makefile
├── Dockerfile
├── config/
│   └── config.go            # Config types, loading, defaults
├── hue/
│   ├── api.go               # Hue API v2 HTTP client
│   ├── sse.go               # SSE event listener with reconnection
│   └── types.go             # Hue resource type definitions
├── state/
│   ├── manager.go           # Resource cache, topic mapping
│   └── event_handler.go     # Event processing, message routing
├── messages/
│   ├── light.go             # Light message conversion
│   ├── button.go            # Button message conversion
│   ├── presence.go          # Motion/presence message conversion
│   ├── temperature.go       # Temperature message conversion
│   ├── ambient.go           # Light level message conversion
│   ├── device_power.go      # Battery message conversion
│   ├── grouped_light.go     # Grouped light message conversion
│   ├── zigbee.go            # Zigbee connectivity message conversion
│   └── zgp.go               # ZGP connectivity message conversion
├── command/
│   ├── handler.go           # MQTT command routing
│   └── effects.go           # Light effect animations
├── topic/
│   └── utils.go             # Topic name sanitization
├── version/
│   └── version.go           # Build version info (ldflags)
```

**Rationale:** Mirrors the logical separation of the TypeScript version. Each package has a clear responsibility. Follows Go conventions (lowercase, short names).

### 3. SSE client: custom implementation using `bufio.Scanner` over `net/http`

**Rationale:** The Hue SSE endpoint is a standard HTTP stream. Go's `net/http` client with a long-lived response body and `bufio.Scanner` is sufficient. No external SSE library needed. This keeps dependencies minimal and allows custom reconnection/watchdog logic matching the TypeScript behavior.

**Alternative considered:** Using `r3labs/sse` or similar libraries. Rejected because the Hue SSE format is simple (just `data:` fields) and adding a dependency for this is unnecessary.

### 4. HTTPS with self-signed certificate: `crypto/tls` with `InsecureSkipVerify`

**Rationale:** Hue bridges use self-signed certificates. The TypeScript version uses `rejectUnauthorized: false`. Go equivalent is `InsecureSkipVerify: true` on the TLS config. This is acceptable for local network communication with a known device.

### 5. Concurrency: `sync.Mutex` for PUT operations

**Rationale:** The TypeScript version uses `async-lock` to serialize PUT requests to the Hue API. In Go, a simple `sync.Mutex` achieves the same. No need for a library.

### 6. Scheduling: `time.Ticker` for hourly full state refresh

**Rationale:** The TypeScript version uses `node-cron` for hourly updates. Go's `time.Ticker` is simpler and built-in. No cron library needed.

### 7. MQTT library: `github.com/philipparndt/mqtt-gateway`

**Rationale:** Matches mqtt-lamarzocco. Wraps `paho.mqtt.golang` with convenient publish/subscribe and config patterns including Last Will and Testament.

### 8. Logger: `github.com/philipparndt/go-logger`

**Rationale:** Matches mqtt-lamarzocco. Structured logging with level configuration.

### 9. Testing: standard `testing` package with table-driven tests

**Rationale:** No external test framework needed. Go's standard testing is sufficient. Use table-driven tests for message conversion functions (many input/output pairs). Use `testify/assert` only if it simplifies assertions significantly.

## Risks / Trade-offs

- **[SSE reconnection complexity]** → The TypeScript EventSource library handles reconnection automatically. Our custom implementation must handle: connection drops, watchdog timeouts, and graceful reconnection. Mitigation: thorough unit tests with mock HTTP servers, and a watchdog goroutine matching the TypeScript behavior.

- **[Hue API v2 type coverage]** → The Hue API has many resource types. We must ensure all types used by the TypeScript version are covered. Mitigation: derive Go structs directly from the TypeScript type definitions, and test with the existing stub data.

- **[Message format compatibility]** → Even subtle differences (JSON field ordering, number precision) could break downstream consumers. Mitigation: unit tests that verify exact JSON output matches the TypeScript version's output for the same input.

- **[Config backward compatibility]** → The config uses kebab-case JSON keys (e.g., `api-key`, `bridge-info`). Go struct tags handle this via `json:"api-key"`. Mitigation: test config loading with the existing example config file.
