import { HueIdentifiable, HueOwnable } from "./general"

export type ZigbeeGreenPowerConnectivityStatus = "connected"|"disconnected"|"connectivity_issue"|"unidirectional_incoming"

/* eslint-disable camelcase */
export type ZigbeeGreenPowerConnectivity = HueIdentifiable & HueOwnable & {
    type: "zgp_connectivity"
    status: ZigbeeGreenPowerConnectivityStatus
    source_id: string
}

export function isZigbeeGreenPowerConnectivity (object: HueIdentifiable): object is ZigbeeGreenPowerConnectivity {
    return object && object.type === "zgp_connectivity"
}
