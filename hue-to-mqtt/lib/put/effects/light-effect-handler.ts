import { Light, isLight } from "../../api/v2/types/light"
import { LightEffectMessage } from "../../messages/light-message"
import { loadTypedById, putLight } from "../../api/v2/hue-api-v2"
import AsyncLock from "async-lock"
import { log } from "../../logger"

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

    await restoreColor(light)
}

const notifyRestore = async (light: Light, effect: LightEffectMessage) => {
    await applyColors(light, effect)

    await putLight(light, {
        on: light.on
    })

    await restoreColor(light)
}
const lock = new AsyncLock({timeout: 5000})
export const applyEffect = async (light: Light, effect: LightEffectMessage) => {
    lock.acquire("effect", async (done) => {
        await applyEffectLocked(light, effect)
        done()
    }, (err) => {
        if (err) {
            log.error(err)
        }
    })
}

const applyEffectLocked = async (light: Light, effect: LightEffectMessage) => {
    const current = await loadTypedById(light.type, light.id)
    if (!current || !isLight(current)) {
        return
    }

    if (effect.effect === "notify_restore") {
        await notifyRestore(current, effect)
    }
    else if (effect.effect === "notify_off") {
        await notifyOff(current, effect)
    }
}
