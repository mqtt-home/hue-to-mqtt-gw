import { isNameable } from "../api/v2/types/general"
import { getTopic, state } from "./state-manager"
import { deviceStubs } from "../api/v2/device-stubs"
import { Device } from "../api/v2/types/device"

describe("State manager", () => {
    test("nameable", async () => {
        expect(isNameable(deviceStubs.room)).toBeTruthy()
        expect(isNameable(deviceStubs.lightWithColor)).toBeTruthy()
    })

    test("topic", async () => {
        expect(getTopic(deviceStubs.room)).toBe("room/my-room")
        expect(getTopic(deviceStubs.lightWithColor)).toBe("light/unassigned/essen-spot-4")

        state.setDevices([deviceStubs.buttonDevice as Device])
        expect(getTopic(deviceStubs.button)).toBe("button/kueche-oben")
    })

    // test("init state manager", async () => {
    //     await initStateManagerFromHue()
    // })
})
