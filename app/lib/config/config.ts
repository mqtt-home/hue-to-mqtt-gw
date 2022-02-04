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
    protocol: "http"|"https"
}

export type Config = {
    mqtt: ConfigMqtt
    hue: ConfigHue
    names: any,
    "hourly-full-update": boolean
}

let appConfig: Config

const mqttDefaults = {
    qos: 1,
    retain: true,
    "bridge-info": true
}

const hueDefaults = {
    port: 443,
    protocol: "https"
}

const configDefaults = {
    "hourly-full-update": true
}

export const applyDefaults = (config: any) => {
    return {
        ...configDefaults,
        ...config,
        hue: { ...hueDefaults, ...config.hue },
        mqtt: { ...mqttDefaults, ...config.mqtt },
        names: config.names ?? {}
    } as Config
}

export const loadConfig = (file: string) => {
    const buffer = fs.readFileSync(file)
    applyConfig(JSON.parse(buffer.toString()))
}

export const applyConfig = (config: any) => {
    appConfig = applyDefaults(config)
}

export const getAppConfig = () => {
    return appConfig
}

export const setTestConfig = (config: Config) => {
    appConfig = config
}
