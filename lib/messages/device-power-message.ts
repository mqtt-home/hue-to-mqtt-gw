import { DevicePower, DevicePowerData } from "../api/v2/types/device-power"

export type DevicePowerMessage = DevicePowerData

export const fromDevicePower = (power: DevicePower) => {
    return power.power_state
}
