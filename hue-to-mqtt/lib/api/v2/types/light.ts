import { HueIdentifiable, HueNameable, HueOwnable } from "./general"

export type ColorXY = {x: number, y: number}
export type Gamut = {red: ColorXY, green: ColorXY, blue: ColorXY}

export type AlertEffectType = "breathe"

export function isLight(object: HueIdentifiable): object is Light {
    return object.type === "light"
}

export type Light = HueIdentifiable & HueOwnable & HueNameable & {
    type: "light"
    on: { on: boolean }
    dimming?: {
        brightness: number,
        min_dim_level?: number
    }
    color_temperature?: {
        mirek: number
        mirek_valid: boolean
        mirek_schema: {
            mirek_maximum: number,
            mirek_minimum: number
        },
    }
    effects?: any
    color?: {
        xy: ColorXY
        gamut?: Gamut,
        gamut_type: "A"|"B"|"C"|"other",
    }
    dynamics: any
    alert: {
        action_values: AlertEffectType[]
    }
    mode: "normal"|"streaming"
    gradient?: any
}
