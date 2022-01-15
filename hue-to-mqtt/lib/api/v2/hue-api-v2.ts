import axios from "axios"
import https from "https"
import config from "../../config.json"
import { Light } from "./types/light"
import { Device } from "./types/device"
import { Room } from "./types/room"
import { Button } from "./types/button"
import { Result } from "./types/general"
import { DevicePower } from "./types/device-power"
import { Motion } from "./types/motion"

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

export const loadLights: () => Promise<Result<Light>> = async () => {
    return (await load("resource/light"))
}

export const loadDevices: () => Promise<Result<Device>> = async () => {
    return (await load("resource/device"))
}

export const loadButtons: () => Promise<Result<Button>> = async () => {
    return (await load("resource/button"))
}

export const loadRooms: () => Promise<Result<Room>> = async () => {
    return (await load("resource/room"))
}

export const loadDevicePower: () => Promise<Result<DevicePower>> = async () => {
    return (await load("resource/device_power"))
}

export const loadMotion: () => Promise<Result<Motion>> = async () => {
    return (await load("resource/motion"))
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
