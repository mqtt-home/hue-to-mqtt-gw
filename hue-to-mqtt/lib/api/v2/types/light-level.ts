import { HueIdentifiable, HueOwnable } from "./general"

export type LightLevel = HueIdentifiable & HueOwnable & {
    type?: "light_level"
    enabled: boolean
    light: {
        light_level: number
        light_level_valid: boolean
    }
}
