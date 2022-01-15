import { HueIdentifiable, HueOwnable } from "./general"

export type Temperature = HueIdentifiable & HueOwnable & {
    type?: "temperature"
    enabled: boolean
    temperature: {
        temperature: number
        temperature_valid: boolean
    }
}
