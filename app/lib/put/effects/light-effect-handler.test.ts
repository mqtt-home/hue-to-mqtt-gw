import { applyEffect } from "./light-effect-handler"
import { deviceStubs } from "../../api/v2/device-stubs"
import { Light } from "../../api/v2/types/light"
import * as api from "../../api/v2/hue-api-v2"
import { applyDefaults, setTestConfig } from "../../config/config"
import { LightEffectMessage } from "../../messages/light-message"

let messages: any[]

jest.spyOn(api, "loadTypedById").mockReturnValue(Promise.resolve(deviceStubs.lightWithColor))
jest.spyOn(api, "putLight").mockImplementation((x, message) => {
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

        await applyEffect(deviceStubs.lightWithColor as Light, effect)

        expect(messages).toStrictEqual([
            {
                color: {
                    gamut_type: "C",
                    xy: effect.colors[0]
                },
                on: { on: true }
            },
            {
                color: {
                    gamut_type: "C",
                    xy: effect.colors[1]
                },
                on: { on: true }
            },
            { color: deviceStubs.lightWithColor.color }
        ])
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

        await applyEffect(deviceStubs.lightWithColor as Light, effect)

        expect(messages).toStrictEqual([
            {
                color: {
                    gamut_type: "C",
                    xy: effect.colors[0]
                },
                on: { on: true }
            },
            {
                color: {
                    gamut_type: "C",
                    xy: effect.colors[1]
                },
                on: { on: true }
            },
            {
                on: { on: false }
            },
            { color: deviceStubs.lightWithColor.color }
        ])
    })
})
