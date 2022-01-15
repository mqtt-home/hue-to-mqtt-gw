import { HueResource, Resource } from "./general"

export type Temperature = HueResource & {
    type?: "temperature"
    enabled: boolean
    temperature: {
        temperature: number
        temperature_valid: boolean
    }
}
