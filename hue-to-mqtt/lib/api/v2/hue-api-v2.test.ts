import { loadDevices, loadTyped, mapRoomByResourceId } from "./hue-api-v2"
import { Room } from "./types/room"
import { Result } from "./types/general"
import { Button } from "./types/button"
import { Light } from "./types/light"

describe("API v2", () => {
    test("load devices", async () => {
        const devices = await loadDevices()
        for (const device of devices.data) {
            console.log(device.id, device.id_v1)
        }
    })

    test("load rooms", async () => {
        const rooms = (await loadTyped("room")) as Result<Room>
        for (const room of rooms.data) {
            console.log(room.id_v1, room.metadata.name, room.children)
        }
    })

    test("load buttons", async () => {
        const buttons = (await loadTyped("button")) as Result<Button>
        for (const button of buttons.data) {
            console.log(button.metadata.control_id, button.id)
        }
    })

    test("load lights", async () => {
        const rooms = (await loadTyped("room")) as Result<Room>
        const roomByResourceId = mapRoomByResourceId(rooms.data)
        const lights = (await loadTyped("light")) as Result<Light>
        for (const light of lights.data) {
            const room = roomByResourceId.get(light.owner.rid)

            console.log(light.id, light.metadata.name, room?.metadata.name??"none")
        }
    })
})
