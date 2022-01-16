import * as fs from "fs"
import path from "path"

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

export const loadConfig = (file: string) => {
    const buffer = fs.readFileSync(file)
    appConfig = JSON.parse(buffer.toString()) as Config

    if (appConfig.mqtt.qos == null) {
        appConfig.mqtt.qos = 1
    }
    if (appConfig.mqtt.retain == null) {
        appConfig.mqtt.retain = true
    }
    if (appConfig.mqtt["bridge-info"] == null) {
        appConfig.mqtt["bridge-info"] = true
    }
}

export const getAppConfig = () => {
    return appConfig
}
