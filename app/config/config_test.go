package config

import (
	"os"
	"path/filepath"
	"testing"
)

func writeTestConfig(t *testing.T, content string) string {
	t.Helper()
	dir := t.TempDir()
	path := filepath.Join(dir, "config.json")
	err := os.WriteFile(path, []byte(content), 0644)
	if err != nil {
		t.Fatal(err)
	}
	return path
}

func TestLoadConfig_MinimalConfig(t *testing.T) {
	path := writeTestConfig(t, `{
		"mqtt": {"url": "tcp://localhost:1883"},
		"hue": {"host": "192.168.1.1", "api-key": "test-key"}
	}`)

	c, err := LoadConfig(path)
	if err != nil {
		t.Fatal(err)
	}

	if c.MQTT.URL != "tcp://localhost:1883" {
		t.Errorf("expected url tcp://localhost:1883, got %s", c.MQTT.URL)
	}
	if c.Hue.Host != "192.168.1.1" {
		t.Errorf("expected host 192.168.1.1, got %s", c.Hue.Host)
	}
	if c.Hue.APIKey != "test-key" {
		t.Errorf("expected api-key test-key, got %s", c.Hue.APIKey)
	}
}

func TestLoadConfig_Defaults(t *testing.T) {
	path := writeTestConfig(t, `{
		"mqtt": {"url": "tcp://localhost:1883"},
		"hue": {"host": "192.168.1.1", "api-key": "key"}
	}`)

	c, err := LoadConfig(path)
	if err != nil {
		t.Fatal(err)
	}

	if c.MQTT.QoS != 1 {
		t.Errorf("expected default qos 1, got %d", c.MQTT.QoS)
	}
	if !c.MQTT.Retain {
		t.Error("expected default retain true")
	}
	if !c.MQTT.BridgeInfo {
		t.Error("expected default bridge-info true")
	}
	if c.Hue.Port != 443 {
		t.Errorf("expected default port 443, got %d", c.Hue.Port)
	}
	if c.Hue.Protocol != "https" {
		t.Errorf("expected default protocol https, got %s", c.Hue.Protocol)
	}
	if c.Hue.SSEWatchdogMs != 0 {
		t.Errorf("expected default sse-watchdog-millis 0, got %d", c.Hue.SSEWatchdogMs)
	}
	if !c.SendFullUpdate {
		t.Error("expected default send-full-update true")
	}
	if c.LogLevel != "info" {
		t.Errorf("expected default loglevel info, got %s", c.LogLevel)
	}
	if c.Names == nil {
		t.Error("expected names to be initialized")
	}
}

func TestLoadConfig_FullConfig(t *testing.T) {
	path := writeTestConfig(t, `{
		"mqtt": {
			"url": "tcp://broker:1883",
			"topic": "hue",
			"username": "user",
			"password": "pass",
			"retain": false,
			"qos": 2,
			"bridge-info": false,
			"bridge-info-topic": "custom/bridge"
		},
		"hue": {
			"host": "10.0.0.1",
			"api-key": "my-key",
			"port": 8443,
			"protocol": "http",
			"sse-watchdog-millis": 30000
		},
		"names": {"abc-123": "my-light"},
		"send-full-update": false,
		"loglevel": "debug"
	}`)

	c, err := LoadConfig(path)
	if err != nil {
		t.Fatal(err)
	}

	if c.MQTT.Topic != "hue" {
		t.Errorf("expected topic hue, got %s", c.MQTT.Topic)
	}
	if c.MQTT.Username != "user" {
		t.Errorf("expected username user, got %s", c.MQTT.Username)
	}
	if c.MQTT.QoS != 2 {
		t.Errorf("expected qos 2, got %d", c.MQTT.QoS)
	}
	if c.MQTT.Retain {
		t.Error("expected retain false")
	}
	if c.MQTT.BridgeInfo {
		t.Error("expected bridge-info false")
	}
	if c.MQTT.BridgeInfoTopic != "custom/bridge" {
		t.Errorf("expected bridge-info-topic custom/bridge, got %s", c.MQTT.BridgeInfoTopic)
	}
	if c.Hue.Port != 8443 {
		t.Errorf("expected port 8443, got %d", c.Hue.Port)
	}
	if c.Hue.Protocol != "http" {
		t.Errorf("expected protocol http, got %s", c.Hue.Protocol)
	}
	if c.Hue.SSEWatchdogMs != 30000 {
		t.Errorf("expected sse-watchdog-millis 30000, got %d", c.Hue.SSEWatchdogMs)
	}
	if c.Names["abc-123"] != "my-light" {
		t.Errorf("expected name mapping, got %v", c.Names)
	}
	if c.SendFullUpdate {
		t.Error("expected send-full-update false")
	}
	if c.LogLevel != "debug" {
		t.Errorf("expected loglevel debug, got %s", c.LogLevel)
	}
}

func TestLoadConfig_EnvVarSubstitution(t *testing.T) {
	t.Setenv("TEST_MQTT_URL", "tcp://envhost:1883")
	t.Setenv("TEST_API_KEY", "env-api-key")

	path := writeTestConfig(t, `{
		"mqtt": {"url": "${TEST_MQTT_URL}"},
		"hue": {"host": "192.168.1.1", "api-key": "${TEST_API_KEY}"}
	}`)

	c, err := LoadConfig(path)
	if err != nil {
		t.Fatal(err)
	}

	if c.MQTT.URL != "tcp://envhost:1883" {
		t.Errorf("expected env var substitution, got %s", c.MQTT.URL)
	}
	if c.Hue.APIKey != "env-api-key" {
		t.Errorf("expected env var substitution, got %s", c.Hue.APIKey)
	}
}

func TestLoadConfig_MissingFile(t *testing.T) {
	_, err := LoadConfig("/nonexistent/config.json")
	if err == nil {
		t.Error("expected error for missing file")
	}
}

func TestLoadConfig_InvalidJSON(t *testing.T) {
	path := writeTestConfig(t, `{invalid`)
	_, err := LoadConfig(path)
	if err == nil {
		t.Error("expected error for invalid JSON")
	}
}

func TestBridgeInfoTopic_Default(t *testing.T) {
	c := Config{MQTT: MQTTConfig{Topic: "hue"}}
	if c.BridgeInfoTopic() != "hue/bridge/state" {
		t.Errorf("expected hue/bridge/state, got %s", c.BridgeInfoTopic())
	}
}

func TestBridgeInfoTopic_Custom(t *testing.T) {
	c := Config{MQTT: MQTTConfig{Topic: "hue", BridgeInfoTopic: "custom/topic"}}
	if c.BridgeInfoTopic() != "custom/topic" {
		t.Errorf("expected custom/topic, got %s", c.BridgeInfoTopic())
	}
}

func TestToGatewayConfig(t *testing.T) {
	m := MQTTConfig{
		URL:      "tcp://localhost:1883",
		Topic:    "hue",
		Username: "user",
		Password: "pass",
		Retain:   true,
		QoS:      1,
	}
	gw := m.ToGatewayConfig()
	if gw.URL != m.URL || gw.Topic != m.Topic || gw.Username != m.Username || gw.Password != m.Password || gw.Retain != m.Retain || gw.QoS != m.QoS {
		t.Error("gateway config mismatch")
	}
}
