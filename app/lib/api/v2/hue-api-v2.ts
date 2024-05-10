import axios, { AxiosInstance } from "axios"
import https from "https"
import { Device } from "./types/device"
import { HueIdentifiable, Result } from "./types/general"
import { Light, LightColorData, LightColorTemperatureData, LightOnOffData } from "./types/light"
import { log } from "../../logger"
import { getAppConfig } from "../../config/config"
import AsyncLock from "async-lock"
import { resolvable } from "../../concurrency/concurrency"
import { GroupedLight } from "./types/grouped-light"

let instance: AxiosInstance
let baserUrl: string

const getInstance = () => {
    if (!instance) {
        const config = getAppConfig()
        const protocol = config.hue.protocol
        baserUrl = `${protocol}://${config.hue.host}:${config.hue.port}/clip/v2/`

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
    dimming?: {brightness: number},
    on?: LightOnOffData,
    /* eslint-disable camelcase */
    color_temperature?: LightColorTemperatureData,
    color?: LightColorData
}

export const putLightResource = async (resource: Light) => {
    return putLight(resource, {
        dimming: resource.dimming,
        on: resource.on,
        color_temperature: resource.color_temperature,
        color: resource.color
    })
}

export const putGroupedLightResource = async (resource: GroupedLight) => {
    return putLight(resource, {
        dimming: resource.dimming,
        on: resource.on,
        color_temperature: resource.color_temperature,
        color: resource.color
    })
}

const putLightLocked = async (resource: HueIdentifiable, message: PutLight) => {
    const config = getAppConfig()
    const topic = `resource/${resource.type}/${resource.id}`
    console.log(topic, message)

    try {
        const result = await getInstance().put(topic, message, {
            headers: {
                "hue-application-key": config.hue["api-key"],
                Accept: "application/json"
            }
        })
        return result.data
    }
    catch (e) {
        log.error("Put light failed with error", e)
    }
}

const lock = new AsyncLock({ timeout: 5000 })
export const putLight = async (light: HueIdentifiable, message: PutLight) => {
    const [resolveResult, promise] = resolvable()
    lock.acquire("put", async (done) => {
        await putLightLocked(light, message)
        done()
        resolveResult()
    }, (err) => {
        if (err) {
            log.error("Put light failed with error", err)
        }
    })

    return promise
}

export const loadDevices: () => Promise<Result<Device>> | undefined = async () => {
    return load("resource/device")
}

export const loadAllResources: () => Promise<Result<Device>> | undefined = async () => {
    return load("resource")
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
