package state

import (
	"sync"

	"github.com/mqtt-home/hue2mqtt/hue"
	"github.com/mqtt-home/hue2mqtt/messages"
	"github.com/mqtt-home/hue2mqtt/topic"
	"github.com/philipparndt/go-logger"
)

var mgr *Manager

type Manager struct {
	mu              sync.RWMutex
	typedResources  map[string]hue.Resource
	roomByResourceID map[string]hue.Resource
	resourcesByTopic map[string]hue.Resource
	deviceByID      map[string]hue.Resource
	customNames     map[string]string
	baseTopic       string
}

func Init(names map[string]string) {
	mgr = &Manager{
		typedResources:   make(map[string]hue.Resource),
		roomByResourceID: make(map[string]hue.Resource),
		resourcesByTopic: make(map[string]hue.Resource),
		deviceByID:       make(map[string]hue.Resource),
		customNames:      names,
	}
}

func SetBaseTopic(t string) {
	mgr.baseTopic = t
}

func InitFromHue() error {
	mgr.mu.Lock()
	defer mgr.mu.Unlock()

	// Clear existing state
	mgr.typedResources = make(map[string]hue.Resource)
	mgr.roomByResourceID = make(map[string]hue.Resource)
	mgr.resourcesByTopic = make(map[string]hue.Resource)
	mgr.deviceByID = make(map[string]hue.Resource)

	// Load devices first for name resolution
	devices, err := hue.FetchDevices()
	if err != nil {
		return err
	}
	if devices != nil {
		logger.Debug("Loaded devices", "count", len(devices.Data))
		for _, device := range devices.Data {
			mgr.deviceByID[device.ID] = device
			for _, service := range device.Services {
				mgr.deviceByID[service.RID] = device
			}
		}
	}

	// Load rooms
	rooms, err := hue.FetchRooms()
	if err != nil {
		return err
	}
	if rooms != nil {
		logger.Debug("Loaded rooms", "count", len(rooms.Data))
		for _, room := range rooms.Data {
			mgr.addTypedResource(room)
		}
	}

	// Load all other resources
	resources, err := hue.FetchAllResources()
	if err != nil {
		return err
	}
	if resources == nil {
		logger.Error("Failed to load resources")
		return nil
	}

	for _, resource := range resources.Data {
		if !hue.IsRoom(resource) {
			mgr.addTypedResource(resource)
		}
	}

	logger.Debug("State initialized",
		"typedResources", len(mgr.typedResources),
		"topics", len(mgr.resourcesByTopic),
		"devices", len(mgr.deviceByID),
	)

	return nil
}

func (m *Manager) addTypedResource(resource hue.Resource) {
	m.typedResources[resource.ID] = resource

	t := m.getTopic(resource)
	fullTopic := m.baseTopic + "/" + t

	logger.Trace("Adding resource", "type", resource.Type, "id", resource.ID, "topic", fullTopic)

	if hue.IsLight(resource) || hue.IsGroupedLight(resource) {
		m.resourcesByTopic[fullTopic+"/set"] = resource
	}
	m.resourcesByTopic[fullTopic+"/get"] = resource
	m.resourcesByTopic[fullTopic+"/state"] = resource

	if hue.IsRoom(resource) {
		for _, child := range resource.Children {
			m.roomByResourceID[child.RID] = resource
		}
	}
}

func (m *Manager) mapName(resource hue.Resource) string {
	if customName, ok := m.customNames[resource.ID]; ok {
		return customName
	}

	if hue.IsNameable(resource) {
		return resource.Metadata.Name
	}

	// Try to find name via device
	if device, ok := m.deviceByID[resource.ID]; ok && hue.IsNameable(device) {
		return device.Metadata.Name
	}

	return resource.ID
}

func (m *Manager) getTopic(resource hue.Resource) string {
	prefix := resource.Type

	if hue.IsLight(resource) && resource.Owner != nil {
		roomName := "unassigned"
		if room, ok := m.roomByResourceID[resource.Owner.RID]; ok && room.Metadata != nil {
			roomName = room.Metadata.Name
		}
		prefix = prefix + "/" + roomName
	}

	return topic.Clean(prefix + "/" + m.mapName(resource))
}

// GetTopic returns the MQTT topic for a resource (without base topic prefix)
func GetTopic(resource hue.Resource) string {
	mgr.mu.RLock()
	defer mgr.mu.RUnlock()
	return mgr.getTopic(resource)
}

// GetResourceByTopic finds a resource by its full MQTT topic
func GetResourceByTopic(fullTopic string) *hue.Resource {
	mgr.mu.RLock()
	defer mgr.mu.RUnlock()

	if r, ok := mgr.resourcesByTopic[fullTopic]; ok {
		return &r
	}
	return nil
}

// GetAllResources returns all typed resources
func GetAllResources() []hue.Resource {
	mgr.mu.RLock()
	defer mgr.mu.RUnlock()

	resources := make([]hue.Resource, 0, len(mgr.typedResources))
	for _, r := range mgr.typedResources {
		resources = append(resources, r)
	}
	return resources
}

// ConvertToMessage converts a resource to its MQTT message representation
func ConvertToMessage(resource hue.Resource) interface{} {
	switch {
	case hue.IsLight(resource):
		msg := messages.FromLight(resource)
		return msg
	case hue.IsLightLevel(resource):
		msg := messages.FromLightLevel(resource)
		return msg
	case hue.IsButton(resource):
		msg := messages.FromButton(resource)
		return msg
	case hue.IsMotion(resource):
		msg := messages.FromMotion(resource)
		return msg
	case hue.IsTemperature(resource):
		msg := messages.FromTemperature(resource)
		return msg
	case hue.IsDevicePower(resource):
		msg := messages.FromDevicePower(resource)
		return msg
	case hue.IsZgpConnectivity(resource):
		msg := messages.FromZgpConnectivity(resource)
		return msg
	case hue.IsZigbeeConnectivity(resource):
		msg := messages.FromZigbeeConnectivity(resource)
		return msg
	case hue.IsGroupedLight(resource):
		msg := messages.FromGroupedLight(resource)
		return msg
	default:
		return resource
	}
}

// TakeEvent processes an SSE event and publishes updates.
// The publish callback is called outside the lock to avoid deadlocks.
func TakeEvent(event hue.HueEvent, publish func(hue.Resource)) {
	var updated []hue.Resource

	mgr.mu.Lock()
	for _, data := range event.Data {
		oldResource, ok := mgr.typedResources[data.ID]
		if !ok {
			logger.Warn("No resource found for event", "id", data.ID, "type", data.Type)
			continue
		}

		newResource := mergeEventData(oldResource, data)
		mgr.typedResources[data.ID] = newResource
		updated = append(updated, newResource)
	}
	mgr.mu.Unlock()

	for _, r := range updated {
		logger.Trace("Event updated resource", "type", r.Type, "id", r.ID)
		publish(r)
	}
}

func mergeEventData(resource hue.Resource, data hue.HueEventData) hue.Resource {
	if data.On != nil {
		resource.On = data.On
	}
	if data.Dimming != nil {
		resource.Dimming = data.Dimming
	}
	if data.ColorTemperature != nil {
		resource.ColorTemperature = data.ColorTemperature
	}
	if data.Color != nil {
		resource.Color = data.Color
	}
	if data.Motion != nil {
		resource.Motion = data.Motion
	}
	if data.Button != nil {
		resource.Button = data.Button
	}
	if data.Temperature != nil {
		resource.Temperature = data.Temperature
	}
	if data.Light != nil {
		resource.Light = data.Light
	}
	if data.PowerState != nil {
		resource.PowerState = data.PowerState
	}
	if len(data.Status) > 0 {
		resource.Status = data.Status
	}
	if data.Enabled != nil {
		resource.Enabled = data.Enabled
	}
	return resource
}
