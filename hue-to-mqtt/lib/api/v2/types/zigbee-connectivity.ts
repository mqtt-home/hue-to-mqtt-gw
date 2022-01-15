import { HueIdentifiable, HueOwnable } from "./general"

export type ZigbeeConnectivity = HueIdentifiable & HueOwnable & {
    type: "zigbee_connectivity"
    status: "connected"|"disconnected"|"connectivity_issue"|"unidirectional_incoming"
    mac_address: string
}
