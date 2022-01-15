import { HueIdentifiable, HueOwnable } from "./general"

export function isLightLevel(object: HueIdentifiable): object is LightLevel {
    return object && object.type === "light_level"
}

export type LightLevel = HueIdentifiable & HueOwnable & {
    type?: "light_level"
    enabled: boolean
    light: {
        light_level: number
        light_level_valid: boolean
    }
}
