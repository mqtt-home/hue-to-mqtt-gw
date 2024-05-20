import { HueIdentifiable, HueNameable, HueOwnable } from "./general"
import { AlertEffectType, LightColorData, LightColorTemperatureData, LightDimmingData, LightOnOffData } from "./light"

 
export type GroupedLight = HueIdentifiable & HueOwnable & HueNameable & {
    type: "grouped_light"
    on: LightOnOffData
    alert: {
        action_values: AlertEffectType[]
    }
    dimming?: LightDimmingData
    color_temperature?: LightColorTemperatureData
    color?: LightColorData
}

export function isGroupedLight (object?: HueIdentifiable): object is GroupedLight {
    if (!object) {
        return false
    }
    return object && object.type === "grouped_light"
}
