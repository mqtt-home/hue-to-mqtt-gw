import * as fs from "fs"

export type ConfigMqtt = {
    url: string,
    topic: string
    username?: string
    password?: string
    retain: boolean
    qos: (0|1|2)
    "bridge-info"?: boolean
    "bridge-info-topic"?: string
}

export type ConfigHue = {
    host: string
    "api-key": string
    port: number
}

export type Config = {
    mqtt: ConfigMqtt
    hue: ConfigHue
}

let appConfig: Config

const mqttDefaults = {
    qos: 1,
    retain: true,
    "bridge-info": true
}

const hueDefaults = {
    port: 443
}

export const applyDefaults = (config: any) => {
    return {
        hue: {...hueDefaults, ...config.hue},
        mqtt: {...mqttDefaults, ...config.mqtt}
    } as Config
}

export const loadConfig = (file: string) => {
    const buffer = fs.readFileSync(file)
    appConfig = applyDefaults(JSON.parse(buffer.toString()))
}

export const getAppConfig = () => {
    return appConfig
}
