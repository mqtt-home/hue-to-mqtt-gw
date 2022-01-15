import { HueIdentifiable, HueNameable, Resource } from "./general"

export type Rooms = {
    error: string[]
    data: Room[]
}

export type Room = HueIdentifiable & HueNameable & {
    type: "room"
    services: Resource[]
    children: Resource[]
}
