## ADDED Requirements

### Requirement: Fetch all devices from Hue Bridge
The system SHALL fetch all device resources from the Hue API v2 endpoint `GET /clip/v2/resource/device` using the configured host, port, protocol, and API key. The HTTP client SHALL accept self-signed TLS certificates.

#### Scenario: Successful device fetch
- **WHEN** the application requests devices from a reachable Hue Bridge
- **THEN** it SHALL return a list of device resources with their IDs, metadata (name, archetype), and product data

#### Scenario: Unreachable bridge
- **WHEN** the Hue Bridge is not reachable
- **THEN** the system SHALL log an error and return an error without crashing

### Requirement: Fetch all typed resources
The system SHALL fetch resources from the following Hue API v2 endpoints: `/clip/v2/resource/light`, `/clip/v2/resource/button`, `/clip/v2/resource/motion`, `/clip/v2/resource/temperature`, `/clip/v2/resource/light_level`, `/clip/v2/resource/device_power`, `/clip/v2/resource/grouped_light`, `/clip/v2/resource/zigbee_connectivity`, `/clip/v2/resource/zgp_connectivity`.

#### Scenario: Fetch lights
- **WHEN** the application fetches lights from the Hue Bridge
- **THEN** it SHALL return light resources with on/off state, dimming, color temperature, and color XY fields

#### Scenario: Fetch rooms
- **WHEN** the application fetches rooms from `/clip/v2/resource/room`
- **THEN** it SHALL return room resources with their child device references

### Requirement: PUT light resource
The system SHALL send `PUT /clip/v2/resource/light/{id}` requests to update light state on the Hue Bridge. Requests SHALL be serialized (one at a time) to avoid race conditions.

#### Scenario: Update light state
- **WHEN** a light state update is requested with on/off, brightness, color_temp, or color fields
- **THEN** the system SHALL send a PUT request with the appropriate JSON body and the `hue-application-key` header

### Requirement: PUT grouped light resource
The system SHALL send `PUT /clip/v2/resource/grouped_light/{id}` requests to update grouped light state.

#### Scenario: Update grouped light state
- **WHEN** a grouped light state update is requested
- **THEN** the system SHALL send a PUT request with the on/off state and optional brightness, color_temp, or color fields

### Requirement: Authentication via API key header
All Hue API requests SHALL include the `hue-application-key` header set to the configured API key.

#### Scenario: API key in request header
- **WHEN** any HTTP request is made to the Hue Bridge
- **THEN** the request SHALL include the header `hue-application-key: <configured-api-key>`
