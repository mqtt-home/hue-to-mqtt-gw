import { isLight, Light } from "../api/v2/types/light"
import { Button } from "../api/v2/types/button"
import { Room } from "../api/v2/types/room"
import { mapRoomByResourceId } from "../api/v2/hue-api-v2"
import { cleanTopic } from "../topic/topic-utils"
import { HueIdentifiable, isNameable } from "../api/v2/types/general"

export class StateManager {
    _lights: Light[] = []
    _buttons: Button[] = []
    _rooms: Room[] = []
    roomByResourceId = new Map<string, Room>()
    resourcesByTopic = new Map<string, HueIdentifiable>()

    updateRoomMapping = () => {
        this.roomByResourceId = mapRoomByResourceId(this._rooms)
    }

    setRooms = (rooms: Room[]) => {
        this._rooms = rooms
        this.updateRoomMapping()
    }

    setLights = (lights: Light[]) => {
        this._lights = lights
    }

    setButtons = (buttons: Button[]) => {
        this._buttons = buttons
    }
}

export const getTopic = (resource: HueIdentifiable) => {
    let prefix = resource.type
    if (isLight(resource)) {
        const room = state.roomByResourceId
            .get(resource.owner.rid)?.metadata
            .name??"unassigned"
        prefix = `${prefix}/${room}`
    }

    if (isNameable(resource)) {
        return cleanTopic(`${prefix}/${resource.metadata.name}`)
    }

    return cleanTopic(resource.id)
}

export const getLightTopic = (light: Light) => {
    const room = state.roomByResourceId
        .get(light.owner.rid)?.metadata
        .name??"unassigned"
    return `${room}/${light.metadata.name}`
}

export const state = new StateManager()
