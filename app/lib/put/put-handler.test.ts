import * as api from "../api/v2/hue-api-v2"
import { deviceStubs } from "../api/v2/device-stubs"
import { applyDefaults, setTestConfig } from "../config/config"
import { LightEffectMessage, LightMessage } from "../messages/light-message"
import { putMessage } from "./put-handler"
import { TestLogger } from "../logger.test"
import { expectedForNotifyRestore } from "./effects/light-effect-handler-stubs"

let messages: any[]

jest.spyOn(api, "loadTypedById").mockReturnValue(Promise.resolve(deviceStubs.lightWithColor))
jest.spyOn(api, "putLight").mockImplementation((x, message) => {
    messages.push(message)
    return Promise.resolve()
})

describe("PUT handler", () => {
    let logger: TestLogger

    beforeAll(() => {
        logger = new TestLogger()
    })

    afterEach(() => {
        logger.output = ""
    })

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

    test("PUT effect", async () => {
        const effect: LightEffectMessage = {
            effect: "notify_restore",
            colors: [
                { x: 0.6758, y: 0.2953 },
                { x: 0.2004, y: 0.5948 }
            ],
            duration: 5
        }

        await putMessage(deviceStubs.lightWithColor, Buffer.from(JSON.stringify(effect)))

        expect(messages).toStrictEqual(expectedForNotifyRestore(effect))
    })

    test("PUT invalid message", async () => {
        const message = "invalid-message"

        await putMessage(deviceStubs.lightWithColor, Buffer.from(message))

        expect(messages.length).toBe(0)
        expect(logger.output).toContain("Unexpected token i in JSON at position 0")
    })

    test("Turn on", async () => {
        const msg: LightMessage = {
            state: "ON",
            brightness: 50
        }

        await putMessage(deviceStubs.lightWithColor, Buffer.from(JSON.stringify(msg)))

        expect(messages).toStrictEqual([
            {
                dimming: { brightness: 50 },
                color: deviceStubs.lightWithColor.color,
                color_temperature: {
                    mirek: null,
                    mirek_schema: {
                        mirek_maximum: 500,
                        mirek_minimum: 153
                    },
                    mirek_valid: true
                },
                on: {
                    on: true
                }
            }
        ])
    })

    test("Turn off", async () => {
        const msg: LightMessage = {
            state: "OFF",
            brightness: 50
        }

        await putMessage(deviceStubs.lightWithColor, Buffer.from(JSON.stringify(msg)))

        expect(messages).toStrictEqual([
            {
                dimming: { brightness: 50 },
                color: deviceStubs.lightWithColor.color,
                color_temperature: {
                    mirek: null,
                    mirek_schema: {
                        mirek_maximum: 500,
                        mirek_minimum: 153
                    },
                    mirek_valid: true
                },
                on: {
                    on: false
                }
            }
        ])
    })

    test("Set color temperature", async () => {
        const msg: LightMessage = {
            state: "OFF",
            brightness: 100,
            color_temp: 250
        }

        await putMessage(deviceStubs.lightWithColor, Buffer.from(JSON.stringify(msg)))
        expect(messages).toStrictEqual([
            {
                dimming: {
                    brightness: 100
                },
                on: {
                    on: false
                },
                color: undefined,
                color_temperature: {
                    mirek: 250,
                    mirek_schema: {
                        mirek_maximum: 500,
                        mirek_minimum: 153
                    },
                    mirek_valid: true
                }
            }
        ])
    })
})
