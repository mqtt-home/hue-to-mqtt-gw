export type Result<T> = {
    error: string[]
    data: T[]
}

export type HueIdentifiable = {
    type?: string
    id: string
    id_v1?: string
}

export type HueOwnable = {
    owner: Resource
}

export function isOwnable(object: any): object is HueOwnable {
    return object && "owner" in object
}

export type HueNameable = {
    metadata: Metadata
}

export function isNameable(object: any): object is HueNameable {
    if (!object) {
        return false
    }
    else if ("metadata" in object) {
        return "name" in object.metadata
    }
    return false
}

export type ResourceType = (
    "device"|
    "bridge_home"|
    "room"|
    "zone"|
    "light"|
    "button"|
    "temperature"|
    "light_level"|
    "motion"|
    "entertainment"|
    "grouped_light"|
    "device_power"|
    "zigbee_bridge_connectivity"|
    "zigbee_connectivity"|
    "zgp_connectivity"|
    "bridge"|
    "homekit"|
    "scene"|
    "entertainment_configuration"|
    "public_image"|
    "auth_v1"|
    "behavior_script"|
    "behavior_instance"|
    "geofence"|
    "geofence_client"|
    "geolocation"
)

export type Archetype = string

export type Resource = {rid: string, rtype: ResourceType}
export type Metadata = {archetype: Archetype, name: string}
