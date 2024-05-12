import { applyEffect } from "./light-effect-handler"
import { deviceStubs } from "../../api/v2/device-stubs"
import { Light } from "../../api/v2/types/light"
import * as api from "../../api/v2/hue-api-v2"
import { applyDefaults, setTestConfig } from "../../config/config"
import { LightEffectMessage } from "../../messages/light-message"
import { expectedForNotifyOff, expectedForNotifyRestore } from "./light-effect-handler-stubs"
import { beforeEach, describe, expect, test, vi } from "vitest"

let messages: any[]
const currentLight = { ...deviceStubs.lightWithColor }

vi.spyOn(api, "loadTypedById").mockReturnValue(Promise.resolve(currentLight))
vi.spyOn(api, "putLight").mockImplementation((x, message) => {
    messages.push(message)
    return Promise.resolve()
})

describe("Light effects", () => {
    beforeEach(() => {
        messages = []

        setTestConfig(applyDefaults({
            hue: {
                port: 80,
                host: "localhost",
                "api-key": "none",
                protocol: "http"
            },
            mqtt: {
                url: "tcp://localhost:1883",
                topic: "hue"
            }
        }))
    })

    test("notify restore", async () => {
        const effect: LightEffectMessage = {
            effect: "notify_restore",
            colors: [
                { x: 0.6758, y: 0.2953 },
                { x: 0.2004, y: 0.5948 }
            ],
            duration: 5
        }

        await applyEffect(currentLight as Light, effect)
        expect(messages).toStrictEqual(expectedForNotifyRestore(effect))
    })

    test("notify restore - light off", async () => {
        currentLight.on = { on: false }
        const effect: LightEffectMessage = {
            effect: "notify_restore",
            colors: [
                { x: 0.2004, y: 0.5948 },
                { x: 0.6758, y: 0.2953 }
            ],
            duration: 5
        }

        await applyEffect(currentLight as Light, effect)
        expect(messages).toStrictEqual(expectedForNotifyOff(effect))
    })

    test("notify off", async () => {
        const effect: LightEffectMessage = {
            effect: "notify_off",
            colors: [
                { x: 0.2004, y: 0.5948 },
                { x: 0.6758, y: 0.2953 }
            ],
            duration: 5
        }

        await applyEffect(currentLight as Light, effect)
        expect(messages).toStrictEqual(expectedForNotifyOff(effect))
    })
})
