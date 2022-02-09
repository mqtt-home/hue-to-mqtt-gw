import { BatteryState, DevicePower } from "../api/v2/types/device-power"

export type DevicePowerMessage = {
    battery_level: number
    battery_state: BatteryState
    "last-updated": string
}

export const fromDevicePower = (power: DevicePower) => {
    return {
        ...power.power_state,
        "last-updated": new Date().toISOString()
    } as DevicePowerMessage
}
