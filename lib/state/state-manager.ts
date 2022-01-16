import { isLight } from "../api/v2/types/light"
import { isRoom, Room } from "../api/v2/types/room"
import {
    loadDevices,
    loadTyped
} from "../api/v2/hue-api-v2"
import { cleanTopic } from "../topic/topic-utils"
import { HueIdentifiable, isNameable } from "../api/v2/types/general"
import { Device } from "../api/v2/types/device"
import { log } from "../logger"
import { publishResource } from "./state-event-handler"
import { getAppConfig } from "../config/config"

export class StateManager {
    _typedResources = new Map<string, HueIdentifiable>()

    roomByResourceId = new Map<string, Room>()
    resourcesByTopic = new Map<string, HueIdentifiable>()
    deviceByDeviceId = new Map<string, Device>()

    setDevices = (devices: Device[]) => {
        for (const device of devices) {
            this.deviceByDeviceId.set(device.id, device)
            for (const service of device.services) {
                this.deviceByDeviceId.set(service.rid, device)
            }
        }
    }

    getTyped = () => {
        return this._typedResources.values()
    }

    addRoom = (room: Room) => {
        for (const child of room.children) {
            this.roomByResourceId.set(child.rid, room)
        }
    }

    addTypedResources = (resources: HueIdentifiable[]) => {
        for (const resource of resources) {
            this._typedResources.set(resource.id, resource)
            const topic = getTopic(resource)
            const fullTopic = `${getAppConfig().mqtt.topic}/${topic}`
            if (isLight(resource)) {
                this.resourcesByTopic.set(`${fullTopic}/set`, resource)
            }
            this.resourcesByTopic.set(`${fullTopic}/get`, resource)
            this.resourcesByTopic.set(`${fullTopic}/state`, resource)

            if (isRoom(resource)) {
                this.addRoom(resource)
            }
        }
    }
}

const updateAll = async () => {
    log.info("Sending full update")

    for (const resource of state.getTyped()) {
        publishResource(resource)
    }
    log.info("Sending full update done")
}

export const initStateManagerFromHue = async () => {
    state.setDevices((await loadDevices()).data)

    // Rooms first
    for (const typeName of ["room"]) {
        state.addTypedResources((await loadTyped(typeName)).data)
    }

    for (const typeName of ["light", "light_level", "bridge_home",
        "grouped_light", "bridge", "device_power", "zigbee_connectivity", "zgp_connectivity",
        "temperature", "motion", "button"]) {
        state.addTypedResources((await loadTyped(typeName)).data)
    }

    await updateAll()
}

const getNameProvider = (resource: HueIdentifiable) => {
    return state.deviceByDeviceId.get(resource.id)
}

export const getTopic = (resource: HueIdentifiable) => {
    let prefix = resource.type
    if (isLight(resource)) {
        const room = state.roomByResourceId
            .get(resource.owner.rid)?.metadata
            .name ?? "unassigned"
        prefix = `${prefix}/${room}`
    }
    const nameProvider = (isNameable(resource) ? resource : getNameProvider(resource))

    if (isNameable(nameProvider)) {
        return cleanTopic(`${prefix}/${nameProvider.metadata.name}`)
    }

    return cleanTopic(`${prefix}/${resource.id}`)
}

export const state = new StateManager()
