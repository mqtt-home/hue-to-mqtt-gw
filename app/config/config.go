package config

import (
	"encoding/json"
	"os"

	"github.com/philipparndt/go-logger"
	gwconfig "github.com/philipparndt/mqtt-gateway/config"
)

var cfg Config

type MQTTConfig struct {
	URL             string `json:"url"`
	Topic           string `json:"topic"`
	Username        string `json:"username,omitempty"`
	Password        string `json:"password,omitempty"`
	Retain          bool   `json:"retain"`
	QoS             byte   `json:"qos"`
	BridgeInfo      bool   `json:"bridge-info"`
	BridgeInfoTopic string `json:"bridge-info-topic,omitempty"`
}

func (m MQTTConfig) ToGatewayConfig() gwconfig.MQTTConfig {
	return gwconfig.MQTTConfig{
		URL:      m.URL,
		Retain:   m.Retain,
		Topic:    m.Topic,
		QoS:      m.QoS,
		Username: m.Username,
		Password: m.Password,
	}
}

type HueConfig struct {
	Host             string `json:"host"`
	APIKey           string `json:"api-key"`
	Port             int    `json:"port"`
	Protocol         string `json:"protocol"`
	SSEWatchdogMs    int    `json:"sse-watchdog-millis"`
}

type Config struct {
	MQTT           MQTTConfig        `json:"mqtt"`
	Hue            HueConfig         `json:"hue"`
	Names          map[string]string `json:"names"`
	SendFullUpdate bool              `json:"send-full-update"`
	LogLevel       string            `json:"loglevel,omitempty"`
}

func (c Config) BridgeInfoTopic() string {
	if c.MQTT.BridgeInfoTopic != "" {
		return c.MQTT.BridgeInfoTopic
	}
	return c.MQTT.Topic + "/bridge/state"
}

func applyDefaults(c *Config) {
	if c.MQTT.QoS == 0 {
		c.MQTT.QoS = 1
	}

	if c.LogLevel == "" {
		c.LogLevel = "info"
	}

	if c.Hue.Port == 0 {
		c.Hue.Port = 443
	}

	if c.Hue.Protocol == "" {
		c.Hue.Protocol = "https"
	}

	if c.Names == nil {
		c.Names = make(map[string]string)
	}
}

func LoadConfig(file string) (Config, error) {
	data, err := os.ReadFile(file)
	if err != nil {
		logger.Error("Error reading config file", "error", err)
		return Config{}, err
	}

	data = gwconfig.ReplaceEnvVariables(data)

	// Set defaults that need to be true before unmarshaling
	cfg = Config{
		MQTT: MQTTConfig{
			Retain:     true,
			BridgeInfo: true,
		},
		SendFullUpdate: true,
	}

	err = json.Unmarshal(data, &cfg)
	if err != nil {
		logger.Error("Error parsing config", "error", err)
		return Config{}, err
	}

	applyDefaults(&cfg)

	return cfg, nil
}

func Get() Config {
	return cfg
}
