import { HueIdentifiable, Metadata, Resource } from "./general"

export type Device = HueIdentifiable & {
    type: "device"
    services: [Resource],
    product_data: {
        model_id: string,
        product_id: string,
        manufacturer_name: string,
        product_name: string,
        product_archetype: string,
        certified: boolean,
        software_version: string
    }
    metadata: Metadata
    creation_time: string
}
