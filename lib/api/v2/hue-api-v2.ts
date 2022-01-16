import axios, { AxiosInstance } from "axios"
import https from "https"
import { Device } from "./types/device"
import { Room } from "./types/room"
import { HueIdentifiable, Result } from "./types/general"
import { Light, LightColorData, LightColorTemperatureData, LightOnOffData } from "./types/light"
import { log } from "../../logger"
import { getAppConfig } from "../../config/config"

let instance: AxiosInstance
let baserUrl: string

const getInstance = () => {
    if (!instance) {
        const config = getAppConfig()
        baserUrl = `https://${config.hue.host}:${config.hue.port}/clip/v2/`

        instance = axios.create({
            baseURL: baserUrl,
            httpsAgent: new https.Agent({
                rejectUnauthorized: false
            })
        })
    }

    return instance
}

export const load = async (endpoint: string) => {
    try {
        const config = getAppConfig()
        const result = await getInstance().get(endpoint, {
            headers: {
                "hue-application-key": config.hue["api-key"],
                Accept: "application/json"
            }
        })
        return result.data
    }
    catch (e) {
        log.error(`Error fetching data from endpoint: ${baserUrl}/${endpoint} ${e}`)
    }
}

type PutLight = {
    brightness?: number,
    on?: LightOnOffData,
    /* eslint-disable camelcase */
    color_temperature?: LightColorTemperatureData,
    color?: LightColorData
}

export const putResource = async (resource: Light) => {
    return putLight(resource, {
        brightness: resource.dimming?.brightness,
        on: resource.on,
        color_temperature: resource.color_temperature,
        color: resource.color
    })
}

export const putLight = async (resource: Light, message: PutLight) => {
    const config = getAppConfig()
    try {
        const result = await getInstance().put(`resource/light/${resource.id}`, message, {
            headers: {
                "hue-application-key": config.hue["api-key"],
                Accept: "application/json"
            }
        })
        return result.data
    }
    catch (e) {
        log.error(e)
    }
}

export const loadDevices: () => Promise<Result<Device>> | undefined = async () => {
    return load("resource/device")
}

export const loadTyped: (resourceName: string) => Promise<Result<HueIdentifiable>> | undefined = async (resourceName: string) => {
    return load(`resource/${resourceName}`)
}

export const loadTypedById = async (resourceName: string, id: string) => {
    const data: Result<HueIdentifiable> = await load(`resource/${resourceName}/${id}`)
    if (data.data.length === 1) {
        return data.data[0]
    }
    else {
        return undefined
    }
}
