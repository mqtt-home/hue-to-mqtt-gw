import { Light } from "../api/v2/types/light"

export type LightMessage = {
    state: "ON"|"OFF"
    brightness: number
    color_temp?: number
    color?: {x: number, y: number}
}

export const fromLight = (light: Light) => {
    let message: LightMessage = {
        state: light.on.on ? "ON" : "OFF",
        brightness: light.dimming?.brightness??0
    }

    if (light.color_temperature) {
        message.color_temp = light.color_temperature.mirek
    }

    if (light.color) {
        message.color = light.color.xy
    }

    return message
}

export const toLight = (template: Light, message: LightMessage) => {
    let result = {...template}

    result.on.on = message.state.toUpperCase() === "ON"
    result.dimming = {brightness: message.brightness}

    if (message.color_temp && template.color_temperature) {
        result.color_temperature = {...template.color_temperature, mirek: message.color_temp}
    }
    if (message.color && template.color) {
        result.color = {...template.color, xy: message.color}
    }

    return result
}
