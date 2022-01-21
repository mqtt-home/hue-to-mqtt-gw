import { Light, isLight } from "../../api/v2/types/light"
import { LightEffectMessage } from "../../messages/light-message"
import { loadTypedById, putLight } from "../../api/v2/hue-api-v2"
import AsyncLock from "async-lock"
import { log } from "../../logger"

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

    // if (light.on && light.on.on) {
    //     // I don't like to get a flickering with the original color,
    //     // so restore color is not possible when the light was previously off.
    //     await restoreColor(light)
    // }
    // else {
    //     await putLight(light, {
    //         on: light.on
    //     })
    // }
}

const resolvable = () => {
    let resolveResult: any
    const promise = new Promise(resolve => {
        resolveResult = resolve
    })
    return [resolveResult, promise]
}

const lock = new AsyncLock({ timeout: 5000 })
export const applyEffect = async (light: Light, effect: LightEffectMessage) => {
    const [resolveResult, promise] = resolvable()
    lock.acquire("effect", async (done) => {
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
