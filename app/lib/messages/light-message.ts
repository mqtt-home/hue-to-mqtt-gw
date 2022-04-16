import { ColorXY, Light } from "../api/v2/types/light"
import { GroupedLight } from "../api/v2/types/grouped-light"

/* eslint-disable camelcase */
export type LightMessage = {
    state: "ON"|"OFF"
    brightness: number
    color_temp?: number
    color?: ColorXY
}

export type LightEffectMessage = {
    effect: "notify_restore"|"notify_off"
    colors: ColorXY[]
    duration: number
}

export function isEffectMessage (object: LightMessage | LightEffectMessage): object is LightEffectMessage {
    return object && "effect" in object
}

export const fromLight = (light: Light) => {
    const message: LightMessage = {
        state: light.on.on ? "ON" : "OFF",
        brightness: Math.round(light.dimming?.brightness ?? 0)
    }

    if (light.color_temperature && light.color_temperature.mirek && light.color_temperature.mirek_valid) {
        message.color_temp = light.color_temperature.mirek
    }
    else if (light.color) {
        message.color = light.color.xy
    }

    return message
}

export const toLight = (template: Light, message: LightMessage) => {
    const result = { ...template }

    result.on.on = message.state.toUpperCase() === "ON"
    result.dimming = { brightness: message.brightness }

    if (message.color_temp && template.color_temperature) {
        result.color_temperature = { ...template.color_temperature, mirek: message.color_temp }
        delete result.color
    }
    else if (message.color && template.color) {
        delete result.color_temperature
        delete result.dimming
        result.color = { xy: message.color }
    }

    return result
}

export const toGroupedLight = (template: GroupedLight, message: LightMessage) => {
    const result = { ...template }

    result.on.on = message.state.toUpperCase() === "ON"

    if (message.color_temp && template.color_temperature) {
        result.color_temperature = { ...template.color_temperature, mirek: message.color_temp }
        delete result.color
    }
    else if (message.color && template.color) {
        delete result.color_temperature
        delete result.dimming
        result.color = { xy: message.color }
    }

    return result
}
