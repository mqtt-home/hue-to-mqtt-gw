import { buttonDevice, buttonStub, lightStub, roomStub } from "./device-stubs"
import { isNameable } from "../api/v2/types/general"
import { getTopic, initStateManagerFromHue, state } from "./state-manager"
import { startSSE } from "../SSEClient"
import { log } from "../logger"
import cron from "node-cron"
import { triggerFullUpdate } from "../index"

describe("State manager", () => {
    // test("debug", async () => {
    //     await triggerFullUpdate()
    //
    //     const sse = startSSE()
    //     sse.addEventListener("message", event => {
    //         for (const data of JSON.parse(event.data)) {
    //             state.takeEvent(data)
    //         }
    //     })
    // })
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
