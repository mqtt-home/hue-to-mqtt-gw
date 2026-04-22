## Context

The Go app already imports `_ "net/http/pprof"` and runs `http.ListenAndServe(":6060", nil)`. The `expvar` package automatically registers its handler at `/debug/vars` on the default mux, so importing `"expvar"` is all that's needed to expose the endpoint. We just need to publish meaningful variables.

## Goals / Non-Goals

**Goals:**
- Expose device counts by resource type
- Track MQTT messages published (total counter)
- Track SSE events received and processed
- Expose uptime and last-full-update timestamp
- Expose SSE connection state

**Non-Goals:**
- Prometheus/OpenMetrics format (expvar JSON is sufficient)
- Per-topic message counters (too many topics, would bloat memory)
- Historical data or time-series (this is point-in-time introspection)

## Decisions

### 1. Central `metrics` package with expvar variables

Create a `metrics` package that owns all `expvar.Int`, `expvar.Map`, and `expvar.String` variables. Other packages call `metrics.IncrMessagePublished()` etc. rather than touching expvar directly. This keeps metrics concerns out of business logic.

**Alternative considered:** Putting expvar calls inline in each package. Rejected because it scatters metric names and makes it hard to see what's exposed.

### 2. Use `expvar.Map` for device counts

A single `expvar.Map` named `"devices"` holds per-type counts: `{"light": 12, "button": 4, ...}`. Updated after each `InitFromHue()` call.

### 3. Atomic counters for messages and events

`expvar.Int` is already goroutine-safe (uses `atomic.Int64` internally). No additional locking needed.

## Risks / Trade-offs

- **[Metric staleness]** Device counts are only updated on full refresh (startup + hourly). This is acceptable since devices rarely change. → No mitigation needed.
