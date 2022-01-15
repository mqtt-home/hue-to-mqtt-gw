import { HueIdentifiable, Metadata, Resource } from "./general"

export type Rooms = {
    error: string[]
    data: Room[]
}

export type Room = HueIdentifiable & {
    type: "room"
    services: [Resource]
    metadata: Metadata
    children: [Resource]
}
