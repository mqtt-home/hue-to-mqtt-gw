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

export function isMotion (object: HueIdentifiable): object is Motion {
    return object && object.type === "motion"
}
