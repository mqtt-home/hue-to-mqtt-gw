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

export function isTemperature (object: HueIdentifiable): object is Temperature {
    return object && object.type === "temperature"
}
