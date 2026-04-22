package colors

// ColorXY represents a CIE XY color coordinate
type ColorXY struct {
	X float64 `json:"x"`
	Y float64 `json:"y"`
}

// ColorRGB represents an RGB color
type ColorRGB struct {
	R int `json:"r"`
	G int `json:"g"`
	B int `json:"b"`
}
