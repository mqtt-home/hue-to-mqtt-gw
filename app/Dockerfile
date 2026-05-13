# syntax=docker/dockerfile:1.24

# Build stage
FROM golang:1.25-alpine AS builder

WORKDIR /app

# Install dependencies
RUN apk add --no-cache git

# Copy go mod files
COPY go.mod go.sum ./
RUN go mod download

# Copy source code
COPY . .

# Build the binary
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o hue2mqtt .

# Final stage
FROM gcr.io/distroless/static:nonroot

WORKDIR /

# Copy the binary from builder stage
COPY --from=builder /app/hue2mqtt /hue2mqtt

# Create a non-root user (distroless already provides this)
USER nonroot:nonroot

ENTRYPOINT ["/hue2mqtt", "/var/lib/huemqtt/config.json"]
