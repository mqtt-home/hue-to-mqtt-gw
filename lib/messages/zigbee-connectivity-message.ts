import { ZigbeeConnectivity, ZigbeeConnectivityStatus } from "../api/v2/types/zigbee-connectivity"

export type ZigbeeConnectivityMessage = {
    status: ZigbeeConnectivityStatus
    "last-updated": string
}

export const fromZigbeeConnectivity = (connectivity: ZigbeeConnectivity) => {
    return {
        status: connectivity.status,
        "last-updated": new Date().toISOString()
    } as ZigbeeConnectivityMessage
}
