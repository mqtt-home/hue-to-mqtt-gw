export type Result<T> = {
    error: string[]
    data: T[]
}

export type HueIdentifiable = {
    type?: string
    id: string
    id_v1?: string
}

export type HueResource = HueIdentifiable & {
    owner: Resource
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
