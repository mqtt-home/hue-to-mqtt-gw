import { buttonDevice, buttonStub, lightStub, roomStub } from "./device-stubs"
import { isNameable } from "../api/v2/types/general"
import { getTopic, initStateManagerFromHue, state } from "./state-manager"

describe("State manager", () => {
    test("nameable", async () => {
        expect(isNameable(roomStub)).toBeTruthy()
        expect(isNameable(lightStub)).toBeTruthy()
    })

    test("topic", async () => {
        expect(getTopic(roomStub)).toBe("room/my-room")
        expect(getTopic(lightStub)).toBe("light/unassigned/essen-spot-4")

        state.setDevices([buttonDevice])
        expect(getTopic(buttonStub)).toBe("button/kueche-oben")
    })

    test("init state manager", async () => {
        await initStateManagerFromHue()
        for (const button of state.getButtons()) {
            const device = state.deviceByDeviceId.get(button.id)
            if (device) {
                console.log(getTopic(button), button.button?.last_event, button.metadata.control_id, device.metadata.name)
            }
        }
    })
})
