package command

import (
	"encoding/json"
	"sync"
	"sync/atomic"
	"testing"
	"time"

	"github.com/mqtt-home/hue2mqtt/hue"
	"github.com/mqtt-home/hue2mqtt/messages"
)

func TestPutMessage_UnsupportedType(t *testing.T) {
	r := &hue.Resource{Type: "motion", ID: "m-1"}
	err := PutMessage(r, []byte(`{}`))
	if err != nil {
		t.Errorf("expected no error for unsupported type, got %v", err)
	}
}

func TestPutMessage_InvalidJSON(t *testing.T) {
	r := &hue.Resource{Type: "light", ID: "l-1"}
	err := PutMessage(r, []byte(`{invalid`))
	if err == nil {
		t.Error("expected error for invalid JSON")
	}
}

func TestIsEffectMessage(t *testing.T) {
	tests := []struct {
		name string
		data map[string]interface{}
		want bool
	}{
		{
			"effect message",
			map[string]interface{}{"effect": "notify_restore", "colors": []interface{}{}, "duration": 500},
			true,
		},
		{
			"normal message",
			map[string]interface{}{"state": "ON", "brightness": 100},
			false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := messages.IsEffectMessage(tt.data); got != tt.want {
				t.Errorf("IsEffectMessage() = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestLightMessageParsing(t *testing.T) {
	data := `{"state":"ON","brightness":80,"color_temp":350}`
	var msg messages.LightMessage
	if err := json.Unmarshal([]byte(data), &msg); err != nil {
		t.Fatal(err)
	}

	if msg.State != "ON" {
		t.Errorf("expected ON, got %s", msg.State)
	}
	if msg.Brightness != 80 {
		t.Errorf("expected 80, got %d", msg.Brightness)
	}
	if msg.ColorTemp == nil || *msg.ColorTemp != 350 {
		t.Errorf("expected 350, got %v", msg.ColorTemp)
	}
}

func TestEffectMutex_SerializesConcurrentEffects(t *testing.T) {
	// Verify that effectMu prevents concurrent effects from interleaving.
	// We test the mutex directly: two goroutines try to lock it, and we
	// verify they don't overlap.
	var running atomic.Int32
	var maxConcurrent atomic.Int32
	var wg sync.WaitGroup

	for i := 0; i < 3; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			effectMu.Lock()
			cur := running.Add(1)
			// Track max concurrency
			for {
				old := maxConcurrent.Load()
				if cur <= old || maxConcurrent.CompareAndSwap(old, cur) {
					break
				}
			}
			// Simulate effect work
			time.Sleep(10 * time.Millisecond)
			running.Add(-1)
			effectMu.Unlock()
		}()
	}

	wg.Wait()

	if maxConcurrent.Load() > 1 {
		t.Errorf("effects ran concurrently: max=%d, expected 1", maxConcurrent.Load())
	}
}

func TestEffectMessageParsing(t *testing.T) {
	data := `{"effect":"notify_restore","colors":[{"x":0.7,"y":0.3}],"duration":500}`
	var msg messages.LightEffectMessage
	if err := json.Unmarshal([]byte(data), &msg); err != nil {
		t.Fatal(err)
	}

	if msg.Effect != "notify_restore" {
		t.Errorf("expected notify_restore, got %s", msg.Effect)
	}
	if len(msg.Colors) != 1 {
		t.Fatalf("expected 1 color, got %d", len(msg.Colors))
	}
	if msg.Colors[0].X != 0.7 || msg.Colors[0].Y != 0.3 {
		t.Errorf("unexpected color: %v", msg.Colors[0])
	}
	if msg.Duration != 500 {
		t.Errorf("expected 500, got %d", msg.Duration)
	}
}
