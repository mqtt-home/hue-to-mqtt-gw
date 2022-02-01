import { isButton } from "./types/button"
import { HueIdentifiable } from "./types/general"
import { isRoom } from "./types/room"
import { isDevicePower } from "./types/device-power"
import { isLightLevel } from "./types/light-level"
import { deviceStubs } from "./device-stubs"
import { isMotion } from "./types/motion"
import { isTemperature } from "./types/temperature"
import { isZigbeeGreenPowerConnectivity } from "./types/zgp-connectivity"
import { isZigbeeConnectivity } from "./types/zigbee-connectivity"
import { isLight } from "./types/light"
import { isGroupedLight } from "./types/grouped-light"

describe("API v2", () => {
    const assertTypeCheck = (type: string, method: ((resource: HueIdentifiable) => boolean)) => {
        let found = false
        for (const [, resource] of Object.entries(deviceStubs)) {
            const isType = resource.type === type
            found = found || isType
            expect(method(resource)).toBe(isType)
        }
        expect(found).toBeTruthy()
    }

    test("check types", async () => {
        assertTypeCheck("light", isLight)
        assertTypeCheck("button", isButton)
        assertTypeCheck("room", isRoom)
        assertTypeCheck("device_power", isDevicePower)
        assertTypeCheck("light_level", isLightLevel)
        assertTypeCheck("motion", isMotion)
        assertTypeCheck("temperature", isTemperature)
        assertTypeCheck("zgp_connectivity", isZigbeeGreenPowerConnectivity)
        assertTypeCheck("zigbee_connectivity", isZigbeeConnectivity)
        assertTypeCheck("grouped_light", isGroupedLight)
    })
})
