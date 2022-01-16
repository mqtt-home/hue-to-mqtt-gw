import { HueIdentifiable, HueNameable, Resource } from "./general"

export type Room = HueIdentifiable & HueNameable & {
    type: "room"
    services: Resource[]
    children: Resource[]
}

export function isRoom (object: HueIdentifiable): object is Room {
    return object && object.type === "room"
}
