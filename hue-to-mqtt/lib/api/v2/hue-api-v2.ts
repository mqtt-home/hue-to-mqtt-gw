import axios from "axios"
import https from "https"
import config from "../../config.json"
import { Light } from "./types/light"
import { Device } from "./types/device"
import { Room } from "./types/room"
import { Button } from "./types/button"
import { HueIdentifiable, Result } from "./types/general"
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



export const loadDevices: () => Promise<Result<Device>> = async () => {
    return load("resource/device")
}

// export const loadLights: () => Promise<Result<Light>> = async () => {
//     return load("resource/light")
// }
// export const loadButtons: () => Promise<Result<Button>> = async () => {
//     return load("resource/button")
// }
//
// export const loadRooms: () => Promise<Result<Room>> = async () => {
//     return load("resource/room")
// }
//
// export const loadDevicePower: () => Promise<Result<DevicePower>> = async () => {
//     return load("resource/device_power")
// }
//
// export const loadMotion: () => Promise<Result<Motion>> = async () => {
//     return load("resource/motion")
// }
//
// export const loadTemperature: () => Promise<Result<Light>> = async () => {
//     return (await loadTyped("light")) as Promise<Result<Light>>
// }

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
