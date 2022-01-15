import axios from "axios"
import https from "https"
import config from "../../config.json"
import { Device } from "./types/device"
import { Room } from "./types/room"
import { HueIdentifiable, Result } from "./types/general"
import { Light } from "./types/light"

let baserUrl = `https://${config.hue.host}:${config.hue.port}/clip/v2/`

const instance = axios.create({
    baseURL: baserUrl,
    httpsAgent: new https.Agent({
        rejectUnauthorized: false,
    })
})

export const load = async (endpoint: string) => {
    const result = await instance.get(endpoint, {
        headers: {
            "hue-application-key": config.hue["api-key"],
            "Accept": "application/json"
        }
    })
    return result.data
}

export const putResource = async (resource: Light) => {
    const message = {
        brightness: resource.dimming?.brightness,
        on: resource.on,
        color_temperature: resource.color_temperature,
        color: resource.color
    }

    const result = await instance.put(`resource/light/${resource.id}`, message,{
        headers: {
            "hue-application-key": config.hue["api-key"],
            "Accept": "application/json"
        }
    })
    return result.data
}

export const loadDevices: () => Promise<Result<Device>> = async () => {
    return load("resource/device")
}

export const loadTyped: (resourceName: string) => Promise<Result<HueIdentifiable>> = async (resourceName: string) => {
    return load(`resource/${resourceName}`)
}

export const mapRoomByResourceId = (rooms: Room[]) => {
    const result = new Map<string, Room>()
    for (const room of rooms) {
        for (const resource of room.children) {
            result.set(resource.rid, room)
        }
    }
    return result
}
