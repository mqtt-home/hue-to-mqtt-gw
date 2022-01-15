import { HueResource, Metadata } from "./general"

export type ColorXY = {x: number, y: number}
export type Gamut = {red: ColorXY, green: ColorXY, blue: ColorXY}

export type AlertEffectType = "breathe"

export type Light = HueResource & {
    type: "light"
    metadata: Metadata
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
    gradient: any
}
