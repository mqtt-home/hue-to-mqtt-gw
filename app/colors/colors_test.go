package colors

import (
	"encoding/json"
	"testing"
)

func TestColorXY_JSON(t *testing.T) {
	c := ColorXY{X: 0.3127, Y: 0.3290}
	data, err := json.Marshal(c)
	if err != nil {
		t.Fatal(err)
	}

	var decoded ColorXY
	if err := json.Unmarshal(data, &decoded); err != nil {
		t.Fatal(err)
	}

	if decoded.X != 0.3127 || decoded.Y != 0.3290 {
		t.Errorf("expected {0.3127, 0.3290}, got {%v, %v}", decoded.X, decoded.Y)
	}
}

func TestColorRGB_JSON(t *testing.T) {
	c := ColorRGB{R: 255, G: 128, B: 0}
	data, err := json.Marshal(c)
	if err != nil {
		t.Fatal(err)
	}

	var decoded ColorRGB
	if err := json.Unmarshal(data, &decoded); err != nil {
		t.Fatal(err)
	}

	if decoded.R != 255 || decoded.G != 128 || decoded.B != 0 {
		t.Errorf("expected {255, 128, 0}, got {%d, %d, %d}", decoded.R, decoded.G, decoded.B)
	}
}
