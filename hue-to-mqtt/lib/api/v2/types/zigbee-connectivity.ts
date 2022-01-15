import { HueResource, Resource } from "./general"

export type ZigbeeConnectivity = HueResource & {
    type: "zigbee_connectivity"
    status: "connected"|"disconnected"|"connectivity_issue"|"unidirectional_incoming"
    mac_address: string
}
