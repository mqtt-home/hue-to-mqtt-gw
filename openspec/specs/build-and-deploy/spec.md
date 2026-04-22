## ADDED Requirements

### Requirement: Makefile with standard targets
The project SHALL include a Makefile with the following targets: `build` (build Go binary), `test` (run unit tests), `coverage` (generate coverage report), `lint` (run linter), `docker` (build Docker image), `clean` (remove build artifacts), `deps` (download and tidy dependencies), `run` (build and run with production config), `version` (display version info).

#### Scenario: Build binary
- **WHEN** `make build` is run
- **THEN** a statically linked binary SHALL be produced in the `build/` directory with version info embedded via ldflags

#### Scenario: Run tests
- **WHEN** `make test` is run
- **THEN** all unit tests SHALL be executed with verbose output

#### Scenario: Build Docker image
- **WHEN** `make docker` is run
- **THEN** a Docker image SHALL be built using the multi-stage Dockerfile

### Requirement: Version information via ldflags
The binary SHALL embed version, git commit, and build time via Go linker flags (`-ldflags`), accessible at runtime through a `version` package.

#### Scenario: Version output
- **WHEN** the application starts
- **THEN** it SHALL log the version, git commit, and build time

### Requirement: Multi-stage Dockerfile
The project SHALL include a Dockerfile with: a Go builder stage that compiles a static binary (`CGO_ENABLED=0`), and a final stage using `gcr.io/distroless/static:nonroot` that runs the binary with the config file path.

#### Scenario: Docker image size
- **WHEN** the Docker image is built
- **THEN** the final image SHALL be based on distroless and contain only the binary

#### Scenario: Docker entrypoint
- **WHEN** the container starts
- **THEN** the entrypoint SHALL be the binary with `/var/lib/hue2mqtt/config.json` as the argument

### Requirement: pprof endpoint
The application SHALL start a pprof HTTP server on port 6060 for runtime profiling, using the `net/http/pprof` blank import pattern.

#### Scenario: pprof available
- **WHEN** the application is running
- **THEN** profiling data SHALL be accessible at `http://localhost:6060/debug/pprof/`

### Requirement: Graceful shutdown
The application SHALL handle SIGTERM and SIGINT signals, cleanly disconnecting from the MQTT broker and closing the SSE connection before exiting.

#### Scenario: SIGTERM received
- **WHEN** the application receives a SIGTERM signal
- **THEN** it SHALL log "shutting down", close connections, and exit with code 0
