package command

import (
	"sync"
	"time"

	"github.com/mqtt-home/hue2mqtt/hue"
	"github.com/mqtt-home/hue2mqtt/messages"
	"github.com/philipparndt/go-logger"
)

// effectMu serializes effect animations so that concurrent notifications
// for the same (or different) lights don't interleave their multi-step
// sequences (fetch state -> cycle colors -> restore). Without this,
// two effects arriving close together would each snapshot the same
// initial state, step on each other's color changes, and restore
// incorrectly.
var effectMu sync.Mutex

func applyEffect(light hue.Resource, effect messages.LightEffectMessage) error {
	effectMu.Lock()
	defer effectMu.Unlock()

	// Reload current state
	current, err := hue.FetchTypedByID(light.Type, light.ID)
	if err != nil || current == nil || !hue.IsLight(*current) {
		logger.Error("Failed to fetch current light state for effect", "error", err)
		return err
	}

	switch effect.Effect {
	case "notify_restore":
		if current.On != nil && current.On.On {
			return notifyRestore(*current, effect)
		}
		return notifyOff(*current, effect)
	case "notify_off":
		return notifyOff(*current, effect)
	default:
		logger.Warn("Unknown effect", "effect", effect.Effect)
		return nil
	}
}

func applyColors(light hue.Resource, effect messages.LightEffectMessage) error {
	for _, color := range effect.Colors {
		msg := hue.PutMessage{
			On:      &hue.LightOnOffData{On: true},
			Dimming: &hue.LightDimmingData{Brightness: 100},
			Color:   &hue.LightColorData{XY: color},
		}

		if light.Color != nil && light.Color.GamutType != nil {
			msg.Color.GamutType = light.Color.GamutType
		}

		if err := hue.PutLight(light, msg); err != nil {
			return err
		}

		time.Sleep(time.Duration(effect.Duration) * time.Millisecond)
	}
	return nil
}

func restoreColor(light hue.Resource) error {
	if light.ColorTemperature != nil && light.ColorTemperature.Mirek != nil {
		return hue.PutLight(light, hue.PutMessage{
			Dimming:          light.Dimming,
			ColorTemperature: light.ColorTemperature,
		})
	} else if light.Color != nil {
		return hue.PutLight(light, hue.PutMessage{
			Dimming: light.Dimming,
			Color:   light.Color,
		})
	} else if light.Dimming != nil {
		return hue.PutLight(light, hue.PutMessage{
			Dimming: light.Dimming,
		})
	}
	return nil
}

func notifyOff(light hue.Resource, effect messages.LightEffectMessage) error {
	if err := applyColors(light, effect); err != nil {
		return err
	}

	if err := hue.PutLight(light, hue.PutMessage{
		On: &hue.LightOnOffData{On: false},
	}); err != nil {
		return err
	}

	return restoreColor(light)
}

func notifyRestore(light hue.Resource, effect messages.LightEffectMessage) error {
	if err := applyColors(light, effect); err != nil {
		return err
	}
	return restoreColor(light)
}
