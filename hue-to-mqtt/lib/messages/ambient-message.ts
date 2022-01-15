import { LightLevel } from "../api/v2/types/light-level"

export type AmbientMessage = {
    "last-level": number
    "last-updated": string
}

export const fromLightLevel = (lightLevel: LightLevel) => {
    let message: AmbientMessage = {
        "last-level": lightLevel.light.light_level,
        "last-updated": new Date().toISOString()
    }

    return message
}


