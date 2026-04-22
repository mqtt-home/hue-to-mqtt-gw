## Why

The application already exposes pprof on `:6060` for profiling, but there is no way to inspect runtime state - how many devices are loaded, how many messages have been published, whether SSE is connected, etc. Go's `expvar` package provides a standard way to expose these metrics at `/debug/vars`, which is already served by the pprof HTTP server.

## What Changes

- Expose device counts by type (lights, buttons, sensors, etc.) via expvar
- Track and expose the number of MQTT messages published
- Track and expose the number of SSE events received and processed
- Expose uptime and last full update timestamp
- Expose SSE connection status (connected/disconnected, last event time)
- All metrics accessible at `http://localhost:6060/debug/vars`

## Capabilities

### New Capabilities
- `expvar-metrics`: Expvar-based runtime metrics for devices, messages, SSE status, and uptime

### Modified Capabilities

(none)

## Impact

- **Code**: New `metrics` package, minor additions to `main.go`, `state/manager.go`, `hue/sse.go`, and the MQTT publish path
- **Dependencies**: None - `expvar` is in the Go standard library
- **APIs**: New HTTP endpoint at `/debug/vars` (already served by pprof listener)
