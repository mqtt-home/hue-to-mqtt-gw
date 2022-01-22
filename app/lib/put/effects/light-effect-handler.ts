import { Light, isLight } from "../../api/v2/types/light"
import { LightEffectMessage } from "../../messages/light-message"
import { loadTypedById, putLight } from "../../api/v2/hue-api-v2"
import { log } from "../../logger"
import { lock, resolvable } from "../../concurrency/concurrency"

const applyColors = async (light: Light, effect: LightEffectMessage) => {
    for (const color of effect.colors) {
        await putLight(light, {
            on: { on: true },
            dimming: { brightness: 100 },
            color: {
                xy: color,
                gamut_type: light.color!.gamut_type
            }
        })

        await new Promise((resolve) => setTimeout(resolve, effect.duration))
    }
}

const restoreColor = async (light: Light) => {
    if (light.color_temperature && light.color_temperature.mirek) {
        await putLight(light, {
            dimming: light.dimming,
            color_temperature: light.color_temperature
        })
    }
    else if (light.color) {
        await putLight(light, {
            dimming: light.dimming,
            color: light.color
        })
    }
    else if (light.dimming) {
        await putLight(light, {
            dimming: light.dimming
        })
    }
}

const notifyOff = async (light: Light, effect: LightEffectMessage) => {
    await applyColors(light, effect)

    await putLight(light, {
        on: { on: false }
    })

    await restoreColor(light)
}

const notifyRestore = async (light: Light, effect: LightEffectMessage) => {
    await applyColors(light, effect)

    await restoreColor(light)
}

export const applyEffect = async (light: Light, effect: LightEffectMessage) => {
    const [resolveResult, promise] = resolvable()
    lock.acquire("lights", async (done) => {
        await applyEffectLocked(light, effect)
        done()
        resolveResult()
    }, (err) => {
        if (err) {
            log.error(err)
        }
    })

    return promise
}

const applyEffectLocked = async (light: Light, effect: LightEffectMessage) => {
    const current = await loadTypedById(light.type, light.id)
    if (!current || !isLight(current)) {
        return
    }

    if (effect.effect === "notify_restore") {
        if (current.on && current.on.on) {
            await notifyRestore(current, effect)
        }
        else {
            await notifyOff(current, effect)
        }
    }
    else if (effect.effect === "notify_off") {
        await notifyOff(current, effect)
    }
}
