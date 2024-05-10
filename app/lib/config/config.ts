import * as fs from "fs"
import { log } from "../logger"

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
    "sse-watchdog-millis": number
}

export type Config = {
    mqtt: ConfigMqtt
    hue: ConfigHue
    names: any,
    "send-full-update": boolean
    loglevel: string
}

let appConfig: Config

const mqttDefaults = {
    qos: 1,
    retain: true,
    "bridge-info": true
}

const hueDefaults = {
    port: 443,
    protocol: "https",
    "sse-watchdog-millis": 0
}

const configDefaults = {
    "send-full-update": true,
    loglevel: "info"
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

export const replaceEnvVariables = (input: string) => {
    const envVariableRegex = /\${([^}]+)}/g

    return input.replace(envVariableRegex, (_, envVarName) => {
        return process.env[envVarName] || ""
    })
}

export const loadConfig = (file: string) => {
    const buffer = fs.readFileSync(file)
    const effectiveConfig = replaceEnvVariables(buffer.toString())
    log.trace("Using config", effectiveConfig)
    log.trace("parsing config")
    applyConfig(JSON.parse(effectiveConfig))
}

export const applyConfig = (config: any) => {
    appConfig = applyDefaults(config)
    log.configure(appConfig.loglevel.toUpperCase())
}

export const getAppConfig = () => {
    return appConfig
}

export const setTestConfig = (config: Config) => {
    appConfig = config
}
