import { HueIdentifiable, HueOwnable } from "./general"

export type ZigbeeGreenPowerConnectivity = HueIdentifiable & HueOwnable & {
    type: "zgp_connectivity"
    status: "connected"|"disconnected"|"connectivity_issue"|"unidirectional_incoming"
    source_id: string
}
