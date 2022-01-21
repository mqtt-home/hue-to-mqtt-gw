import { LightEffectMessage } from "../../messages/light-message"
import { deviceStubs } from "../../api/v2/device-stubs"

export const expectedForNotifyRestore = (effect: LightEffectMessage) => [
    {
        color: {
            gamut_type: "C",
            xy: effect.colors[0]
        },
        dimming: {
            brightness: 100
        },
        on: { on: true }
    },
    {
        color: {
            gamut_type: "C",
            xy: effect.colors[1]
        },
        dimming: {
            brightness: 100
        },
        on: { on: true }
    },
    {
        dimming: deviceStubs.lightWithColor.dimming,
        color: deviceStubs.lightWithColor.color
    }
]

export const expectedForNotifyOff = (effect: LightEffectMessage) => [
    {
        color: {
            gamut_type: "C",
            xy: effect.colors[0]
        },
        dimming: {
            brightness: 100
        },
        on: { on: true }
    },
    {
        color: {
            gamut_type: "C",
            xy: effect.colors[1]
        },
        dimming: {
            brightness: 100
        },
        on: { on: true }
    },
    {
        on: { on: false }
    },
    {
        dimming: deviceStubs.lightWithColor.dimming,
        color: deviceStubs.lightWithColor.color
    }
]
