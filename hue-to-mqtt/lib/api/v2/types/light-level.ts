import { HueResource, Resource } from "./general"

export type LightLevel = HueResource & {
    type?: "light_level"
    enabled: boolean
    light: {
        light_level: number
        light_level_valid: boolean
    }
}
