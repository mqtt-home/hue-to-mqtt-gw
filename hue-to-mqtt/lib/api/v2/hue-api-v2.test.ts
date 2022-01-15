import { loadButtons, loadDevicePower, loadDevices, loadLights, loadRooms, mapRoomByResourceId } from "./hue-api-v2"

describe("API v2", () => {
    test("load devices", async () => {
        const devices = await loadDevices()
        for (const device of devices.data) {
            console.log(device.id, device.id_v1)
        }
    })

    test("load rooms", async () => {
        const rooms = await loadRooms()
        for (const room of rooms.data) {
            console.log(room.id_v1, room.metadata.name, room.children)
        }
    })

    test("load buttons", async () => {
        const buttons = await loadButtons()
        for (const button of buttons.data) {
            console.log(button.metadata.control_id, button.id)
        }
    })

    test("load device power", async () => {
        const result = await loadDevicePower()
        for (const power of result.data) {
            console.log(power.power_state.battery_level, power.power_state.battery_state, power.id)
        }
    })

    test("load lights", async () => {
        const rooms = await loadRooms()
        const roomByResourceId = mapRoomByResourceId(rooms.data)
        const lights = await loadLights()
        for (const light of lights.data) {
            const room = roomByResourceId.get(light.owner.rid)

            console.log(light.id, light.metadata.name, room?.metadata.name??"none")
        }
    })
})
