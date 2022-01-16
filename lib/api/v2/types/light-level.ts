import { HueIdentifiable, HueOwnable } from "./general"

/* eslint-disable camelcase */
export type LightLevel = HueIdentifiable & HueOwnable & {
    type?: "light_level"
    enabled: boolean
    light: {
        light_level: number
        light_level_valid: boolean
    }
}

export function isLightLevel (object: HueIdentifiable): object is LightLevel {
    return object && object.type === "light_level"
}
