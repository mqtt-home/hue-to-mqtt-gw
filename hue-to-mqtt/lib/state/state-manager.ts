import { isLight, Light } from "../api/v2/types/light"
import { Button } from "../api/v2/types/button"
import { Room } from "../api/v2/types/room"
import { loadButtons, loadDevices, loadLights, loadRooms, mapRoomByResourceId } from "../api/v2/hue-api-v2"
import { cleanTopic } from "../topic/topic-utils"
import { HueIdentifiable, HueNameable, isNameable } from "../api/v2/types/general"
import { Device } from "../api/v2/types/device"

export class StateManager {
    _lights = new Map<string, Light>()
    _buttons = new Map<string, Button>()
    _rooms: Room[] = []

    roomByResourceId = new Map<string, Room>()
    resourcesByTopic = new Map<string, HueIdentifiable>()
    deviceByDeviceId = new Map<string, Device>()

    updateRoomMapping = () => {
        this.roomByResourceId = mapRoomByResourceId(this._rooms)
    }

    setDevices = (devices: Device[]) => {
        for (const device of devices) {
            this.deviceByDeviceId.set(device.id, device)
            for (const service of device.services) {
                this.deviceByDeviceId.set(service.rid, device)
            }
        }
    }

    setRooms = (rooms: Room[]) => {
        this._rooms = rooms
        this.updateRoomMapping()
    }

    setLights = (lights: Light[]) => {
        const map = new Map<string, Light>()
        for (const light of lights) {
            map.set(light.id, light)
        }
        this._lights = map
        this.putTopicMapping(lights)
    }

    getLights = () => {
        return this._lights.values()
    }

    setButtons = (buttons: Button[]) => {
        const map = new Map<string, Button>()
        for (const button of buttons) {
            map.set(button.id, button)
        }
        this._buttons = map

        this.putTopicMapping(buttons)
    }

    getButtons = () => {
        return this._buttons.values()
    }

    private putTopicMapping(identifiables: HueIdentifiable[]) {
        for (const identifiable of identifiables) {
            const topic = getTopic(identifiable)
            this.resourcesByTopic.set(`${topic}/set`, identifiable)
            this.resourcesByTopic.set(`${topic}/get`, identifiable)
        }
    }
}


export const initStateManagerFromHue = async () => {
    state.setDevices((await loadDevices()).data)
    state.setRooms((await loadRooms()).data)
    state.setButtons((await loadButtons()).data)
    state.setLights((await loadLights()).data)
}

const getNameProvider = (resource: HueIdentifiable) => {
    return state.deviceByDeviceId.get(resource.id)
}

export const getTopic = (resource: HueIdentifiable) => {
    let prefix = resource.type
    if (isLight(resource)) {
        const room = state.roomByResourceId
            .get(resource.owner.rid)?.metadata
            .name??"unassigned"
        prefix = `${prefix}/${room}`
    }
    const nameProvider = (isNameable(resource) ? resource : getNameProvider(resource))

    if (isNameable(nameProvider)) {
        return cleanTopic(`${prefix}/${nameProvider.metadata.name}`)
    }

    return cleanTopic(resource.id)
}

export const state = new StateManager()
