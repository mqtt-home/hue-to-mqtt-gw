import { HueResource, Resource } from "./general"

export type ZigbeeGreenPowerConnectivity = HueResource & {
    type: "zgp_connectivity"
    status: "connected"|"disconnected"|"connectivity_issue"|"unidirectional_incoming"
    source_id: string
}
