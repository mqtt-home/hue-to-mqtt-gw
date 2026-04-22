package state

import (
	"testing"
	"time"

	"github.com/mqtt-home/hue2mqtt/hue"
)

func setupTestManager() {
	Init(map[string]string{
		"custom-id": "my-custom-light",
	})
	SetBaseTopic("hue")
}

func TestGetTopic_Light(t *testing.T) {
	setupTestManager()

	// Add a room first
	room := hue.Resource{
		Type:     "room",
		ID:       "room-1",
		Metadata: &hue.Metadata{Name: "Office"},
		Children: []hue.ResourceRef{{RID: "dev-1", RType: "device"}},
	}
	mgr.addTypedResource(room)

	// Add a light owned by the room device
	light := hue.Resource{
		Type:     "light",
		ID:       "light-1",
		Owner:    &hue.ResourceRef{RID: "dev-1", RType: "device"},
		Metadata: &hue.Metadata{Name: "Desk Lamp"},
		On:       &hue.LightOnOffData{On: true},
	}
	mgr.addTypedResource(light)

	topic := mgr.getTopic(light)
	if topic != "light/office/desk-lamp" {
		t.Errorf("expected light/office/desk-lamp, got %s", topic)
	}
}

func TestGetTopic_CustomName(t *testing.T) {
	setupTestManager()

	light := hue.Resource{
		Type:     "light",
		ID:       "custom-id",
		Owner:    &hue.ResourceRef{RID: "dev-x", RType: "device"},
		Metadata: &hue.Metadata{Name: "Original Name"},
		On:       &hue.LightOnOffData{On: true},
	}
	mgr.addTypedResource(light)

	topic := mgr.getTopic(light)
	if topic != "light/unassigned/my-custom-light" {
		t.Errorf("expected light/unassigned/my-custom-light, got %s", topic)
	}
}

func TestGetTopic_Button(t *testing.T) {
	setupTestManager()

	btn := hue.Resource{
		Type:           "button",
		ID:             "btn-1",
		ButtonMetadata: &hue.ButtonMetadata{ControlID: 1},
	}

	// Button is not nameable and no device found, so uses ID
	topic := mgr.getTopic(btn)
	if topic != "button/btn-1" {
		t.Errorf("expected button/btn-1, got %s", topic)
	}
}

func TestGetTopic_ButtonWithDevice(t *testing.T) {
	setupTestManager()

	device := hue.Resource{
		Type:     "device",
		ID:       "dev-btn",
		Metadata: &hue.Metadata{Name: "Dimmer Switch"},
		Services: []hue.ResourceRef{{RID: "btn-2", RType: "button"}},
	}
	mgr.deviceByID[device.ID] = device
	mgr.deviceByID["btn-2"] = device

	btn := hue.Resource{
		Type:           "button",
		ID:             "btn-2",
		ButtonMetadata: &hue.ButtonMetadata{ControlID: 1},
	}

	topic := mgr.getTopic(btn)
	if topic != "button/dimmer-switch" {
		t.Errorf("expected button/dimmer-switch, got %s", topic)
	}
}

func TestGetResourceByTopic(t *testing.T) {
	setupTestManager()

	light := hue.Resource{
		Type:     "light",
		ID:       "light-1",
		Owner:    &hue.ResourceRef{RID: "dev-1", RType: "device"},
		Metadata: &hue.Metadata{Name: "Test"},
		On:       &hue.LightOnOffData{On: true},
	}
	mgr.addTypedResource(light)

	r := GetResourceByTopic("hue/light/unassigned/test/set")
	if r == nil {
		t.Fatal("expected resource")
	}
	if r.ID != "light-1" {
		t.Errorf("expected light-1, got %s", r.ID)
	}

	r = GetResourceByTopic("nonexistent")
	if r != nil {
		t.Error("expected nil for unknown topic")
	}
}

func TestMergeEventData(t *testing.T) {
	resource := hue.Resource{
		Type:    "light",
		ID:      "light-1",
		On:      &hue.LightOnOffData{On: false},
		Dimming: &hue.LightDimmingData{Brightness: 50},
	}

	data := hue.HueEventData{
		ID: "light-1",
		On: &hue.LightOnOffData{On: true},
	}

	merged := mergeEventData(resource, data)
	if !merged.On.On {
		t.Error("expected on=true after merge")
	}
	if merged.Dimming.Brightness != 50 {
		t.Error("expected brightness preserved")
	}
}

func TestTakeEvent(t *testing.T) {
	setupTestManager()

	light := hue.Resource{
		Type:     "light",
		ID:       "light-1",
		Owner:    &hue.ResourceRef{RID: "dev-1", RType: "device"},
		Metadata: &hue.Metadata{Name: "Test"},
		On:       &hue.LightOnOffData{On: false},
		Dimming:  &hue.LightDimmingData{Brightness: 50},
	}
	mgr.typedResources["light-1"] = light

	published := []hue.Resource{}
	event := hue.HueEvent{
		Data: []hue.HueEventData{
			{
				ID: "light-1",
				On: &hue.LightOnOffData{On: true},
			},
		},
	}

	TakeEvent(event, func(r hue.Resource) {
		published = append(published, r)
	})

	if len(published) != 1 {
		t.Fatalf("expected 1 published, got %d", len(published))
	}
	if !published[0].On.On {
		t.Error("expected on=true in published resource")
	}
	if published[0].Dimming.Brightness != 50 {
		t.Error("expected brightness preserved")
	}
}

func TestTakeEvent_UnknownResource(t *testing.T) {
	setupTestManager()

	event := hue.HueEvent{
		Data: []hue.HueEventData{
			{ID: "unknown-id"},
		},
	}

	published := []hue.Resource{}
	TakeEvent(event, func(r hue.Resource) {
		published = append(published, r)
	})

	if len(published) != 0 {
		t.Error("expected no published for unknown resource")
	}
}

func TestTakeEvent_PublishCallsGetTopic(t *testing.T) {
	setupTestManager()
	SetBaseTopic("hue")

	light := hue.Resource{
		Type:     "light",
		ID:       "light-1",
		Owner:    &hue.ResourceRef{RID: "dev-1", RType: "device"},
		Metadata: &hue.Metadata{Name: "Test"},
		On:       &hue.LightOnOffData{On: false},
		Dimming:  &hue.LightDimmingData{Brightness: 50},
	}
	mgr.typedResources["light-1"] = light

	event := hue.HueEvent{
		Data: []hue.HueEventData{
			{
				ID: "light-1",
				On: &hue.LightOnOffData{On: true},
			},
		},
	}

	// This mimics the real publish callback which calls GetTopic and
	// ConvertToMessage - both take a read lock. Before the deadlock fix
	// this would hang forever because TakeEvent held the write lock.
	done := make(chan struct{})
	go func() {
		TakeEvent(event, func(r hue.Resource) {
			topic := GetTopic(r)
			msg := ConvertToMessage(r)
			if topic == "" || msg == nil {
				t.Error("expected topic and message")
			}
		})
		close(done)
	}()

	select {
	case <-done:
		// success
	case <-time.After(2 * time.Second):
		t.Fatal("TakeEvent deadlocked - publish callback could not acquire read lock")
	}
}

func TestConvertToMessage_Light(t *testing.T) {
	r := hue.Resource{
		Type:    "light",
		On:      &hue.LightOnOffData{On: true},
		Dimming: &hue.LightDimmingData{Brightness: 100},
	}
	msg := ConvertToMessage(r)
	if msg == nil {
		t.Error("expected message")
	}
}

func TestConvertToMessage_GroupedLight(t *testing.T) {
	r := hue.Resource{
		Type: "grouped_light",
		On:   &hue.LightOnOffData{On: true},
	}
	msg := ConvertToMessage(r)
	if msg == nil {
		t.Error("expected message")
	}
}
