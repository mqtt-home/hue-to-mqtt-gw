import { HueIdentifiable, HueOwnable } from "./general"

export function isDevicePower(object: HueIdentifiable): object is DevicePower {
    return object && object.type === "device_power"
}

export type DevicePowerData = {
    battery_state: "normal"|"low"|"critical"
    battery_level: number
}

export type DevicePower = HueIdentifiable & HueOwnable & {
    type: "device_power"
    power_state: DevicePowerData
}
