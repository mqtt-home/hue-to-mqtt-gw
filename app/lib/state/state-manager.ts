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
import { isTrigger } from "../api/v2/types/button"
import { isGroupedLight } from "../api/v2/types/grouped-light"

export class StateManager {
    _typedResources = new Map<string, HueIdentifiable>()

    roomByResourceId = new Map<string, Room>()
    resourcesByTopic = new Map<string, HueIdentifiable>()
    deviceByDeviceId = new Map<string, Device>()

    setDevices = (devices: Device[]) => {
        for (const device of devices) {
            log.debug(device.metadata.name, device.id)
            this.deviceByDeviceId.set(device.id, device)
            for (const service of device.services) {
                this.deviceByDeviceId.set(service.rid, device)
            }
        }
    }

    getTyped = () => {
        return this._typedResources
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
            if (isLight(resource) || isGroupedLight(resource)) {
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
    if (getAppConfig()["send-full-update"]) {
        log.info("Sending full update")

        for (const resource of state.getTyped().values()) {
            // Never update triggers (buttons, motion sensors), otherwise we would fire events again
            if (!isTrigger(resource)) {
                publishResource(resource)
            }
        }
        log.info("Sending full update done")
    }
}

export const initStateManagerFromHue = async () => {
    const devices = await loadDevices()
    if (devices) {
        state.setDevices(devices.data)
    }

    // Rooms first
    const rooms = await loadTyped("room")
    if (rooms) {
        state.addTypedResources(rooms.data)
    }

    for (const typeName of [
        "bridge",
        "bridge_home",
        "button",
        "device_power",
        "device_power",
        "device_software_update",
        "entertainment",
        "grouped_light",
        "light",
        "light_level",
        "motion",
        "relative_rotary",
        "temperature",
        "zgp_connectivity",
        "zigbee_connectivity",
        "grouped_motion",
        "grouped_light_level",
        "scene"
    ]) {
        const resources = await loadTyped(typeName)
        if (resources) {
            state.addTypedResources(resources.data)
        }
    }

    await updateAll()
}

const getNameProvider = (resource: HueIdentifiable) => {
    return state.deviceByDeviceId.get(resource.id)
}

const mapName = (resource: HueIdentifiable) => {
    const customName = getAppConfig()?.names[resource.id]
    if (customName != null) {
        return customName
    }

    const nameProvider = (isNameable(resource) ? resource : getNameProvider(resource))
    if (isNameable(nameProvider)) {
        return nameProvider.metadata.name
    }
    else {
        return resource.id
    }
}

export const getTopic = (resource: HueIdentifiable) => {
    let prefix = resource.type

    if (isLight(resource)) {
        const room = state.roomByResourceId
            .get(resource.owner.rid)?.metadata
            .name ?? "unassigned"
        prefix = `${prefix}/${room}`
    }

    return cleanTopic(`${prefix}/${mapName(resource)}`)
}

export const state = new StateManager()
