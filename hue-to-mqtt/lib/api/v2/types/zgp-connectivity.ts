import { HueIdentifiable, HueOwnable } from "./general"

export function isZigbeeGreenPowerConnectivity(object: HueIdentifiable): object is ZigbeeGreenPowerConnectivity {
    return object && object.type === "zgp_connectivity"
}

export type ZigbeeGreenPowerConnectivity = HueIdentifiable & HueOwnable & {
    type: "zgp_connectivity"
    status: "connected"|"disconnected"|"connectivity_issue"|"unidirectional_incoming"
    source_id: string
}
