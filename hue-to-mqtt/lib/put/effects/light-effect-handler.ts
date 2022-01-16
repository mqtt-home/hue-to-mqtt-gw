import { Light } from "../../api/v2/types/light"
import { LightEffectMessage } from "../../messages/light-message"
import { putLight } from "../../api/v2/hue-api-v2"

const applyColors = async (light: Light, effect: LightEffectMessage) => {
    for (let color of effect.colors) {
        await putLight(light, {
            on: {on: true},
            color: {
                xy: color,
                gamut_type: light.color!.gamut_type
            }
        })

        await new Promise(r => setTimeout(r, effect.duration))
    }
}

const restoreColor = async (light: Light) => {
    if (light.color_temperature && light.color_temperature.mirek) {
        await putLight(light, {
            color_temperature: light.color_temperature
        })
    }

    if (light.color) {
        await putLight(light, {
            color: light.color
        })
    }
}

const notifyOff = async (light: Light, effect: LightEffectMessage) => {
    await applyColors(light, effect)

    await putLight(light, {
        on: {on: false}
    })

    if (light.color_temperature) {
        await putLight(light, {
            color_temperature: {...light.color_temperature!, mirek: 366},
        })
    }
}

const notifyRestore = async (light: Light, effect: LightEffectMessage) => {
    console.log("Initial", light.color_temperature, light.color_temperature?.mirek, light.color)
    await applyColors(light, effect)

    await putLight(light, {
        on: light.on
    })

    await restoreColor(light)
}


export const applyEffect = async (light: Light, effect: LightEffectMessage) => {
    if (effect.effect === "notify_restore") {
        await notifyRestore(light, effect)
    }
    else if (effect.effect === "notify_off") {
        await notifyOff(light, effect)
    }
}
