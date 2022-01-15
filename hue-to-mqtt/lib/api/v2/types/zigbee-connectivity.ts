import { HueIdentifiable, HueOwnable } from "./general"

export function isZigbeeConnectivity(object: HueIdentifiable): object is ZigbeeConnectivity {
    return object && object.type === "zigbee_connectivity"
}

export type ZigbeeConnectivity = HueIdentifiable & HueOwnable & {
    type: "zigbee_connectivity"
    status: "connected"|"disconnected"|"connectivity_issue"|"unidirectional_incoming"
    mac_address: string
}
