package main

import (
	"encoding/json"
	_ "expvar"
	"net/http"
	_ "net/http/pprof"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"

	"github.com/mqtt-home/hue2mqtt/command"
	"github.com/mqtt-home/hue2mqtt/config"
	"github.com/mqtt-home/hue2mqtt/hue"
	"github.com/mqtt-home/hue2mqtt/metrics"
	"github.com/mqtt-home/hue2mqtt/state"
	"github.com/mqtt-home/hue2mqtt/version"
	"github.com/philipparndt/go-logger"
	"github.com/philipparndt/mqtt-gateway/mqtt"
)

func publishState(resource hue.Resource) {
	cfg := config.Get()
	topic := state.GetTopic(resource)
	if topic == "" {
		logger.Trace("Skipping resource with empty topic", "type", resource.Type, "id", resource.ID)
		return
	}

	fullTopic := cfg.MQTT.Topic + "/" + topic
	msg := state.ConvertToMessage(resource)
	if msg == nil {
		logger.Trace("Skipping resource with nil message", "type", resource.Type, "id", resource.ID)
		return
	}

	data, err := json.Marshal(msg)
	if err != nil {
		logger.Error("Failed to marshal message", "error", err)
		return
	}

	logger.Trace("Publishing", "topic", fullTopic, "payload", string(data))
	mqtt.PublishAbsolute(fullTopic, string(data), cfg.MQTT.Retain)
	metrics.IncrMessagesPublished()
}

func handleMQTTMessage(topic string, payload []byte) {
	resource := state.GetResourceByTopic(topic)
	if resource == nil {
		return
	}

	if strings.HasSuffix(topic, "/get") || strings.HasSuffix(topic, "/state") {
		publishState(*resource)
	} else if strings.HasSuffix(topic, "/set") {
		err := command.PutMessage(resource, payload)
		if err != nil {
			logger.Error("Failed to handle set command", "error", err)
		}
	}
}

func triggerFullUpdate() {
	logger.Info("Updating devices")
	err := state.InitFromHue()
	if err != nil {
		logger.Error("Failed to update devices", "error", err)
		return
	}

	resources := state.GetAllResources()
	deviceCounts := make(map[string]int64)
	for _, r := range resources {
		if r.Type != "" {
			deviceCounts[r.Type]++
		}
	}
	metrics.UpdateDeviceCounts(deviceCounts)
	metrics.SetLastFullUpdate(time.Now())

	cfg := config.Get()
	if cfg.SendFullUpdate {
		logger.Info("Sending full update")
		for _, r := range resources {
			if !hue.IsTrigger(r) {
				publishState(r)
			}
		}
		logger.Info("Sending full update done", "count", len(resources))
	}
}

func subscribeToTopics() {
	cfg := config.Get()
	topic := cfg.MQTT.Topic + "/#"
	logger.Info("Subscribing to MQTT", "topic", topic)
	mqtt.Subscribe(topic, handleMQTTMessage)
}

func publishBridgeOnline() {
	cfg := config.Get()
	if cfg.MQTT.BridgeInfo {
		bridgeTopic := cfg.BridgeInfoTopic()
		mqtt.PublishAbsolute(bridgeTopic, "online", cfg.MQTT.Retain)
	}
}

func initPprof() {
	go func() {
		http.ListenAndServe(":6060", nil)
	}()
}

func main() {
	logger.Init("info", logger.Logger())
	metrics.SetStartedAt()
	logger.Info("hue2mqtt", "version", version.Version, "commit", version.GitCommit, "built", version.BuildTime)

	if len(os.Args) < 2 {
		logger.Error("Expected config file as argument")
		os.Exit(1)
	}

	configFile := os.Args[1]
	logger.Info("Loading config", "file", configFile)

	cfg, err := config.LoadConfig(configFile)
	if err != nil {
		logger.Error("Failed to load config", "error", err)
		os.Exit(1)
	}

	logger.SetLevel(cfg.LogLevel)

	initPprof()

	// Initialize Hue API client
	hue.InitClient(cfg.Hue)

	// Initialize state manager with custom names
	state.Init(cfg.Names)
	state.SetBaseTopic(cfg.MQTT.Topic)

	// Start MQTT connection (blocks until connected)
	mqtt.Start(cfg.MQTT.ToGatewayConfig(), "hue2mqtt")

	publishBridgeOnline()
	subscribeToTopics()

	// Initial device fetch and publish
	triggerFullUpdate()

	// Start SSE listener
	stopSSE := hue.StartSSE(cfg.Hue, hue.SSEOptions{
		OnEvent: func(event hue.HueEvent) {
			metrics.IncrSSEEventsReceived()
			metrics.SetSSELastEvent(time.Now())
			state.TakeEvent(event, publishState)
		},
		OnStatus: func(status string) {
			metrics.SetSSEStatus(status)
		},
	})

	// Hourly full state refresh
	ticker := time.NewTicker(1 * time.Hour)
	go func() {
		for range ticker.C {
			triggerFullUpdate()
		}
	}()

	logger.Info("Application is now ready")

	// Wait for shutdown signal
	sigCh := make(chan os.Signal, 1)
	signal.Notify(sigCh, syscall.SIGTERM, syscall.SIGINT)
	<-sigCh

	logger.Info("Shutting down")
	ticker.Stop()
	stopSSE()
}
