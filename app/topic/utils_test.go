package topic

import "testing"

func TestClean(t *testing.T) {
	tests := []struct {
		name  string
		input string
		want  string
	}{
		{"lowercase", "Hello World", "hello-world"},
		{"spaces to dashes", "my light name", "my-light-name"},
		{"umlaut ae", "Küche", "kueche"},
		{"umlaut oe", "Wohnzimmer Öfen", "wohnzimmer-oefen"},
		{"umlaut ue", "Flür", "fluer"},
		{"already clean", "office/desk-lamp", "office/desk-lamp"},
		{"mixed", "Living Room/Über Lamp", "living-room/ueber-lamp"},
		{"multiple spaces", "a  b", "a--b"},
		{"empty", "", ""},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := Clean(tt.input)
			if got != tt.want {
				t.Errorf("Clean(%q) = %q, want %q", tt.input, got, tt.want)
			}
		})
	}
}
