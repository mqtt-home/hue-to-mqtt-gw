import { HueIdentifiable, HueNameable, HueOwnable } from "./general"
import { AlertEffectType, LightOnOffData } from "./light"

/* eslint-disable camelcase */
export type GroupedLight = HueIdentifiable & HueOwnable & HueNameable & {
    type: "grouped_light"
    on: LightOnOffData
    alert: {
        action_values: AlertEffectType[]
    }
}

export function isGroupedLight (object?: HueIdentifiable): object is GroupedLight {
    if (!object) {
        return false
    }
    return object && object.type === "grouped_light"
}
