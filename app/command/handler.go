package command

import (
	"encoding/json"
	"fmt"

	"github.com/mqtt-home/hue2mqtt/hue"
	"github.com/mqtt-home/hue2mqtt/messages"
	"github.com/philipparndt/go-logger"
)

func PutMessage(resource *hue.Resource, payload []byte) error {
	if hue.IsLight(*resource) {
		return putLightMessage(resource, payload)
	} else if hue.IsGroupedLight(*resource) {
		return putGroupedLightMessage(resource, payload)
	}
	return nil
}

func putLightMessage(resource *hue.Resource, payload []byte) error {
	// First check if it's an effect message
	var raw map[string]interface{}
	if err := json.Unmarshal(payload, &raw); err != nil {
		logger.Error("Invalid JSON in set command", "error", err)
		return fmt.Errorf("invalid JSON: %w", err)
	}

	if messages.IsEffectMessage(raw) {
		var effectMsg messages.LightEffectMessage
		if err := json.Unmarshal(payload, &effectMsg); err != nil {
			logger.Error("Invalid effect message", "error", err)
			return fmt.Errorf("invalid effect message: %w", err)
		}
		return applyEffect(*resource, effectMsg)
	}

	var lightMsg messages.LightMessage
	if err := json.Unmarshal(payload, &lightMsg); err != nil {
		logger.Error("Invalid light message", "error", err)
		return fmt.Errorf("invalid light message: %w", err)
	}

	newResource := messages.ToLight(*resource, lightMsg)
	if err := hue.PutLightResource(newResource); err != nil {
		logger.Error("Put light failed", "error", err, "message", lightMsg)
		return err
	}

	return nil
}

func putGroupedLightMessage(resource *hue.Resource, payload []byte) error {
	var lightMsg messages.LightMessage
	if err := json.Unmarshal(payload, &lightMsg); err != nil {
		logger.Error("Invalid grouped light message", "error", err)
		return fmt.Errorf("invalid grouped light message: %w", err)
	}

	newResource := messages.ToGroupedLight(*resource, lightMsg)
	if err := hue.PutGroupedLightResource(newResource); err != nil {
		logger.Error("Put grouped light failed", "error", err, "message", lightMsg)
		return err
	}

	return nil
}
