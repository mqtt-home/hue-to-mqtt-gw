import mqtt from "mqtt"
import { log } from "../logger"
import { state } from "../state/state-manager"
import { publishResource } from "../state/state-event-handler"
import { putMessage } from "../put/put-handler"
import { getAppConfig } from "../config/config"

const makeid = (length: number) => {
    let result = ""
    const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    const charactersLength = characters.length;
    for (let i = 0; i < length; i++ ) {
        result += characters.charAt(Math.floor(Math.random() *
            charactersLength));
    }
    return result;
}

let client: mqtt.MqttClient

export const publish = (message: any, topic: string) => {
    let config = getAppConfig()
    const fullTopic = `${config.mqtt.topic}/${topic}`
    publishAbsolute(message, fullTopic)
}

export const publishAbsolute = (message: any, fullTopic: string) => {
    let config = getAppConfig()
    if (!client) {
        log.error(`MQTT not available, cannot publish to ${fullTopic}`)
        return
    }

    const body = JSON.stringify(message, (key, value) => {
        if (value !== null) return value
    })
    client.publish(fullTopic, body, {retain: config.mqtt.retain})
}

const brideTopic = () => {
    let config = getAppConfig()
    return config.mqtt["bridge-info-topic"]??`${config.mqtt.topic}/bridge/state`
}

const online = () => {
    let config = getAppConfig()
    if (config.mqtt["bridge-info"]) {
        publishAbsolute("online", brideTopic())
    }
}

const willMessage = () => {
    let config = getAppConfig()
    if (config.mqtt["bridge-info"]) {
        return { topic: brideTopic(), payload: "offline", qos: config.mqtt.qos, retain: config.mqtt.retain }
    }
    else {
        return undefined
    }
}

export const connectMqtt = () => {
    let config = getAppConfig()
    const options = {
        clean: true,
        connectTimeout: 4000,
        clientId: makeid(9),
        username: config.mqtt.username,
        password: config.mqtt.password,
        will: willMessage()
    }

    return new Promise((resolve, reject) => {
        client = mqtt.connect(config.mqtt.url, options)
        client.on("connect", function () {
            log.info("MQTT Connected")
            client.subscribe(`${config.mqtt.topic}/#`, (err) => {
                if (!err) {
                    online()
                    log.info("MQTT subscription active")
                    resolve("connected")
                }
                else {
                    log.error(err)
                    reject(err)
                }
            })
        })

        client.on("message",  async (topic, message) => {
            let resource = state.resourcesByTopic.get(topic)
            if (resource) {
                log.info(`MQTT Message received: ${topic}`)

                if (topic.endsWith("/get") || topic.endsWith("/state")) {
                    publishResource(resource)
                }
                else if (topic.endsWith("/set")) {
                    await putMessage(resource, message)
                }
            }
        })
    })


}
