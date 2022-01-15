import { HueIdentifiable, HueOwnable } from "./general"

export type Motion = HueIdentifiable & HueOwnable & {
    type?: "motion"
    enabled: boolean
    motion: {
        motion: boolean
        motion_valid: boolean
    }
}
