import { isLight } from "../../../dist/api/v2/types/light"
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

describe("API v2", () => {
    const assertTypeCheck = (type: string, method: ((resource: HueIdentifiable) => boolean)) => {
        let found = false
        for (let [, resource] of Object.entries(deviceStubs)) {
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
    })

    // test("load devices", async () => {
    //     const devices = await loadDevices()
    //     for (const device of devices.data) {
    //         console.log(device.id, device.id_v1)
    //     }
    // })
    //
    // test("load rooms", async () => {
    //     const rooms = (await loadTyped("room")) as Result<Room>
    //     for (const room of rooms.data) {
    //         console.log(room.id_v1, room.metadata.name, room.children)
    //     }
    // })
    //
    // test("load grouped", async () => {
    //     const resources = (await loadTyped("grouped_light")) as Result<Room>
    //     for (const resource of resources.data) {
    //         console.log(resource.id_v1, resource.children)
    //     }
    // })
    //
    // test("load buttons", async () => {
    //     const buttons = (await loadTyped("button")) as Result<Button>
    //     for (const button of buttons.data) {
    //         console.log(button.metadata.control_id, button.id)
    //     }
    // })
    //
    // test("load lights", async () => {
    //     const rooms = (await loadTyped("room")) as Result<Room>
    //     const roomByResourceId = mapRoomByResourceId(rooms.data)
    //     const lights = (await loadTyped("light")) as Result<Light>
    //     for (const light of lights.data) {
    //         const room = roomByResourceId.get(light.owner.rid)
    //
    //         console.log(light.id, light.metadata.name, room?.metadata.name ?? "none")
    //     }
    // })
})
