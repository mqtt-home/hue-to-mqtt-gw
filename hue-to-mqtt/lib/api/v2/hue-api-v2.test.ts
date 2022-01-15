import { loadButtons, loadDevicePower, loadDevices, loadLights, loadRooms, mapRoomByResourceId } from "./hue-api-v2"
import { getLightTopic, getTopic, state } from "../../state/state-manager"
import { fromLight } from "../../messages/light-message"
import { isNameable } from "./types/general"
import { lightStub, roomStub } from "./hue-api-v2-stubs"

describe("API v2", () => {
    test("nameable", async () => {
        expect(isNameable(roomStub)).toBeTruthy()
        expect(isNameable(lightStub)).toBeTruthy()
    })

    test("topic", async () => {
        expect(getTopic(roomStub)).toBe("room/my-room")
        expect(getTopic(lightStub)).toBe("light/unassigned/essen-spot-4")
    })

    test("init state manager", async () => {
        state.setRooms((await loadRooms()).data)
        state.setButtons((await loadButtons()).data)
        state.setLights((await loadLights()).data)

        for (const light of state._lights) {
            const topic = getLightTopic(light)
            const message = fromLight(light)
            console.log(topic, message)
        }
    })

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
