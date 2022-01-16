import { HueIdentifiable, HueOwnable } from "./general"

export type ZigbeeConnectivityStatus = "connected"|"disconnected"|"connectivity_issue"|"unidirectional_incoming"

/* eslint-disable camelcase */
export type ZigbeeConnectivity = HueIdentifiable & HueOwnable & {
    type: "zigbee_connectivity"
    status: ZigbeeConnectivityStatus
    mac_address: string
}

export function isZigbeeConnectivity (object: HueIdentifiable): object is ZigbeeConnectivity {
    return object && object.type === "zigbee_connectivity"
}
