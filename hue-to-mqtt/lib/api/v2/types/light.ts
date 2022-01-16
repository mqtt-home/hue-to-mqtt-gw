import { HueIdentifiable, HueNameable, HueOwnable } from "./general"

export type ColorXY = {x: number, y: number}
export type Gamut = {red: ColorXY, green: ColorXY, blue: ColorXY}

export type AlertEffectType = "breathe"

export function isLight(object: HueIdentifiable): object is Light {
    return object && object.type === "light"
}

export type LightOnOffData = {
    on: boolean
}

export type LightDimmingData = {
    brightness: number,
    min_dim_level?: number
}

export type LightColorTemperatureData = {
    mirek: number
    mirek_valid: boolean
    mirek_schema: {
        mirek_maximum: number
        mirek_minimum: number
    }
}

export type LightColorData = {
    xy: ColorXY
    gamut?: Gamut
    gamut_type: "A"|"B"|"C"|"other"
}

export type Light = HueIdentifiable & HueOwnable & HueNameable & {
    type: "light"
    on: LightOnOffData
    dimming?: LightDimmingData
    color_temperature?: LightColorTemperatureData
    effects?: any
    color?: LightColorData
    dynamics: any
    alert: {
        action_values: AlertEffectType[]
    }
    mode: "normal"|"streaming"
    gradient?: any
}
