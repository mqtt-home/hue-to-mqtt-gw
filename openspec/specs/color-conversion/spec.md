## ADDED Requirements

### Requirement: XY color representation
The system SHALL represent colors in the CIE XY color space as a struct with `x` and `y` float64 fields, matching the Hue API v2 color format.

#### Scenario: XY color in light message
- **WHEN** a light resource has `color.xy.x: 0.3127` and `color.xy.y: 0.3290`
- **THEN** the JSON output SHALL include `"color":{"x":0.3127,"y":0.3290}`

### Requirement: RGB color representation
The system SHALL provide an RGB color struct with `r`, `g`, `b` integer fields (0-255) for potential future use in color conversion utilities.

#### Scenario: RGB struct fields
- **WHEN** an RGB color is created with r=255, g=128, b=0
- **THEN** the struct SHALL hold values r=255, g=128, b=0
