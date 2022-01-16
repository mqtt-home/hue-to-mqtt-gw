import { ZigbeeGreenPowerConnectivity, ZigbeeGreenPowerConnectivityStatus } from "../api/v2/types/zgp-connectivity"

export type ZgpConnectivityMessage = {
    status: ZigbeeGreenPowerConnectivityStatus
    "last-updated": string
}

export const fromZgpConnectivity = (connectivity: ZigbeeGreenPowerConnectivity) => {
    return {
        status: connectivity.status,
        "last-updated": new Date().toISOString()
    } as ZgpConnectivityMessage
}
