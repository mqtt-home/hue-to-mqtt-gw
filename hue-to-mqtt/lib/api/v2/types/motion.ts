import { HueIdentifiable, HueOwnable } from "./general"

export type MotionData = {
    motion: boolean
    motion_valid: boolean
}

export function isMotion(object: HueIdentifiable): object is Motion {
    return object && object.type === "motion"
}

export type Motion = HueIdentifiable & HueOwnable & {
    type?: "motion"
    enabled: boolean
    motion: MotionData
}
