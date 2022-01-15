import { HueIdentifiable, HueNameable, Resource } from "./general"
import { Light } from "./light"

export type Rooms = {
    error: string[]
    data: Room[]
}

export function isRoom(object: HueIdentifiable): object is Room {
    return object && object.type === "room"
}

export type Room = HueIdentifiable & HueNameable & {
    type: "room"
    services: Resource[]
    children: Resource[]
}
