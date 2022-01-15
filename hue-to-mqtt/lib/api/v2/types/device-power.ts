import { HueResource, Resource } from "./general"

export type DevicePower = HueResource & {
    type: "device_power"
    power_state: {
        battery_state: "normal"|"low"|"critical",
        battery_level: number,
    }
}
