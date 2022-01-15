import { Light } from "../api/v2/types/light"
import { Button } from "../api/v2/types/button"
import { Room } from "../api/v2/types/room"
import { mapRoomByResourceId } from "../api/v2/hue-api-v2"
import { cleanTopic } from "../topic/topic-utils"
import { HueResource } from "../api/v2/types/general"

export class StateManager {
    _lights: Light[] = []
    _buttons: Button[] = []
    _rooms: Room[] = []
    roomByResourceId = new Map<string, Room>()
    resourcesByTopic = new Map<string, HueResource>()

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

export const getTopic = (resource: HueResource) => {
    let prefix = ""
    if (resource.type == "light") {
        const room = state.roomByResourceId
            .get(resource.owner.rid)?.metadata
            .name??"unassigned"
        prefix = `${room}/`
    }

    return `${prefix}/${resource.metadata.name}`

    const room = state.roomByResourceId
        .get(light.owner.rid)?.metadata
        .name??"unassigned"
    return `${room}/${light.metadata.name}`
}

export const getLightTopic = (light: Light) => {
    const room = state.roomByResourceId
        .get(light.owner.rid)?.metadata
        .name??"unassigned"
    return `${room}/${light.metadata.name}`
}

export const state = new StateManager()
