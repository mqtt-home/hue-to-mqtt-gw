import { HueIdentifiable, HueNameable, Resource } from "./general"

/* eslint-disable camelcase */
export type Device = HueIdentifiable & HueNameable & {
    type: "device"
    services: Resource[]
    product_data: {
        model_id: string
        product_id?: string // Deviation from API (optional)
        manufacturer_name: string
        product_name: string
        product_archetype: string
        certified: boolean
        software_version: string
    }
    creation_time?: string // Deviation from API (optional)
}
