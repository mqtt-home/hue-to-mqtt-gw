import { HueIdentifiable, HueOwnable } from "./general"

/* eslint-disable camelcase */
export type DevicePowerData = {
    battery_state: "normal"|"low"|"critical"
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
