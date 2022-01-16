import { Temperature } from "../api/v2/types/temperature"

export type TemperatureMessage = {
    temperature: number
    "last-updated": string
}

export const fromTemperature = (temperature: Temperature) => {
    const message: TemperatureMessage = {
        temperature: Math.round(temperature.temperature.temperature * 100) / 100,
        "last-updated": new Date().toISOString()
    }

    return message
}
