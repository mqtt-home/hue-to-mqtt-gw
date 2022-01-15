import { HueIdentifiable, HueOwnable } from "./general"

export type MotionData = {
    motion: boolean
    motion_valid: boolean
}

export type Motion = HueIdentifiable & HueOwnable & {
    type?: "motion"
    enabled: boolean
    motion: MotionData
}
