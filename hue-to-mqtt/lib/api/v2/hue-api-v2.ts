import axios, { AxiosInstance } from "axios"
import https from "https"
import { Device } from "./types/device"
import { Room } from "./types/room"
import { HueIdentifiable, Result } from "./types/general"
import { Light, LightColorData, LightColorTemperatureData, LightOnOffData } from "./types/light"
import { log } from "../../logger"
import { getAppConfig } from "../../config/config"

let instance: AxiosInstance

const getInstance = () => {
    if (!instance) {
        const config = getAppConfig()
        let baserUrl = `https://${config.hue.host}:${config.hue.port}/clip/v2/`

        instance = axios.create({
            baseURL: baserUrl,
            httpsAgent: new https.Agent({
                rejectUnauthorized: false,
            })
        })
    }

    return instance
}

export const load = async (endpoint: string) => {
    const config = getAppConfig()
    const result = await getInstance().get(endpoint, {
        headers: {
            "hue-application-key": config.hue["api-key"],
            "Accept": "application/json"
        }
    })
    return result.data
}

type PutLight = {
    brightness?: number,
    on?: LightOnOffData,
    color_temperature?: LightColorTemperatureData,
    color?: LightColorData
}

export const putResource = async (resource: Light) => {
    return putLight(resource,  {
        brightness: resource.dimming?.brightness,
        on: resource.on,
        color_temperature: resource.color_temperature,
        color: resource.color
    })
}

export const putLight = async (resource: Light, message: PutLight) => {
    const config = getAppConfig()
    try {
        const result = await getInstance().put(`resource/light/${resource.id}`, message,{
            headers: {
                "hue-application-key": config.hue["api-key"],
                "Accept": "application/json"
            }
        })
        return result.data
    }
    catch (e) {
        log.error(e)
    }
}

export const loadDevices: () => Promise<Result<Device>> = async () => {
    return load("resource/device")
}

export const loadTyped: (resourceName: string) => Promise<Result<HueIdentifiable>> = async (resourceName: string) => {
    return load(`resource/${resourceName}`)
}

export const loadTypedById = async (resourceName: string, id: string) => {
    const data: Result<HueIdentifiable> = await load(`resource/${resourceName}/${id}`)
    if (data.data.length === 1) {
        return data.data[0]
    }
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
