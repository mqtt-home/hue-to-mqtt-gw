import { HueIdentifiable, HueOwnable } from "./general"

export type TemperatureData = {
    temperature: number
    temperature_valid: boolean
}

export type Temperature = HueIdentifiable & HueOwnable & {
    type?: "temperature"
    enabled: boolean
    temperature: TemperatureData
}
