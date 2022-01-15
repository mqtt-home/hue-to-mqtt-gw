import { Temperature } from "../api/v2/types/temperature"

export type TemperatureMessage = {
    temperature: number
    "last-updated": string
}

export const fromTemperature = (temperature: Temperature) => {
    let message: TemperatureMessage = {
        temperature: temperature.temperature.temperature,
        "last-updated": new Date().toISOString()
    }

    return message
}
