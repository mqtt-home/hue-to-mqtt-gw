import { HueResource, Resource } from "./general"

export type Motion = HueResource & {
    type?: "motion"
    enabled: boolean
    motion: {
        motion: boolean
        motion_valid: boolean
    }
}
