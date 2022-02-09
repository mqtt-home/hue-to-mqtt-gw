import { HueIdentifiable, HueOwnable } from "./general"

export type BatteryState = "normal"|"low"|"critical"

/* eslint-disable camelcase */
export type DevicePowerData = {
    battery_state: BatteryState
    battery_level: number
}

/* eslint-disable camelcase */
export type DevicePower = HueIdentifiable & HueOwnable & {
    type: "device_power"
    power_state: DevicePowerData
}

export function isDevicePower (object: HueIdentifiable): object is DevicePower {
    return object && object.type === "device_power"
}
