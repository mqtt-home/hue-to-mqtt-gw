import { deviceStubs } from "../api/v2/device-stubs"
import { fromLightLevel } from "./ambient-message"
import { LightLevel } from "../api/v2/types/light-level"
import { Button } from "../api/v2/types/button"
import { fromButton } from "./button-message"
import { DevicePower } from "../api/v2/types/device-power"
import { fromDevicePower } from "./device-power-message"
import { Motion } from "../api/v2/types/motion"
import { fromMotion } from "./presence-message"
import { Temperature } from "../api/v2/types/temperature"
import { fromTemperature } from "./temperature-message"
import { fromZgpConnectivity } from "./zgp-connectivity-message"
import { ZigbeeGreenPowerConnectivity } from "../api/v2/types/zgp-connectivity"
import { ZigbeeConnectivity } from "../api/v2/types/zigbee-connectivity"
import { fromZigbeeConnectivity } from "./zigbee-connectivity-message"
import { Light } from "../api/v2/types/light"
import { fromLight, isEffectMessage, LightMessage, toLight } from "./light-message"

describe("Messages", () => {
    test("ambient", async () => {
        const message: any = fromLightLevel(deviceStubs.lightLevel as LightLevel)
        delete message["last-updated"]

        expect(message)
            .toStrictEqual({
                "last-level": 22230
            })
    })

    describe("lights", () => {
        test("color", async () => {
            const message: any = fromLight(deviceStubs.lightWithColor as Light)

            expect(message)
                .toStrictEqual({
                    brightness: 100,
                    color: {
                        x: 0.4575,
                        y: 0.4099
                    },
                    color_temp: 366,
                    state: "ON"
                })
        })

        test("ambience", async () => {
            const message: any = fromLight(deviceStubs.lightWithAmbience as Light)

            expect(message)
                .toStrictEqual({
                    brightness: 100,
                    color_temp: 366,
                    state: "OFF"
                })
        })

        test("PUT color", async () => {
            const message: LightMessage = {
                brightness: 100,
                color: {
                    x: 0.1,
                    y: 0.2
                },
                color_temp: 366,
                state: "ON"
            }
            expect(isEffectMessage(message)).toBeFalsy()

            expect(toLight(deviceStubs.lightWithColor as Light, message).color!.xy)
                .toStrictEqual({
                    x: 0.1,
                    y: 0.2
                })
        })

        test("PUT ambience", async () => {
            const message: LightMessage = {
                brightness: 100,
                color_temp: 200,
                state: "OFF"
            }
            expect(isEffectMessage(message)).toBeFalsy()

            expect(toLight(deviceStubs.lightWithAmbience as Light, message).color_temperature!)
                .toStrictEqual({
                    mirek: 200,
                    mirek_schema: {
                        mirek_maximum: 454,
                        mirek_minimum: 153
                    },
                    mirek_valid: true
                })
        })
    })

    test("button", async () => {
        const message: any = fromButton(deviceStubs.button as Button)
        delete message["last-updated"]

        expect(message)
            .toStrictEqual({
                button: 1,
                event: "short_release"
            })
    })

    test("device power", async () => {
        const message: any = fromDevicePower(deviceStubs.devicePower as DevicePower)

        expect(message)
            .toStrictEqual({
                battery_level: 100,
                battery_state: "normal"
            })
    })

    test("presence", async () => {
        const message: any = fromMotion(deviceStubs.motion as Motion)
        delete message["last-updated"]

        expect(message)
            .toStrictEqual({
                presence: true
            })
    })

    test("temperature", async () => {
        const message: any = fromTemperature(deviceStubs.temperature as Temperature)
        delete message["last-updated"]

        expect(message)
            .toStrictEqual({
                temperature: 21.37
            })
    })

    test("zgp", async () => {
        const message: any = fromZgpConnectivity(deviceStubs.zgp as ZigbeeGreenPowerConnectivity)
        delete message["last-updated"]

        expect(message)
            .toStrictEqual({
                status: "unidirectional_incoming"
            })
    })

    test("zigbee", async () => {
        const message: any = fromZigbeeConnectivity(deviceStubs.zigbee as ZigbeeConnectivity)
        delete message["last-updated"]

        expect(message)
            .toStrictEqual({
                status: "connected"
            })
    })
})
