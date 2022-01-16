import { DevicePower } from "../api/v2/types/device-power"

export const fromDevicePower = (power: DevicePower) => {
    return power.power_state
}
