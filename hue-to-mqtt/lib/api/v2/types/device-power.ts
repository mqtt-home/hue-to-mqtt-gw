import { HueIdentifiable, HueOwnable } from "./general"

export type DevicePower = HueIdentifiable & HueOwnable & {
    type: "device_power"
    power_state: {
        battery_state: "normal"|"low"|"critical",
        battery_level: number,
    }
}
