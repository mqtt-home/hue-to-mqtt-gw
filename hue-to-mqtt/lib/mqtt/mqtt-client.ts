import mqtt from "mqtt"
import config from "../config.json"
import { log } from "../logger"
import { state } from "../state/state-manager"
import { publishResource } from "../state/state-event-handler"
import { isEffectMessage, LightEffectMessage, LightMessage, toLight } from "../messages/light-message"
import { putResource } from "../api/v2/hue-api-v2"
import { isLight } from "../api/v2/types/light"
import { putMessage } from "../put/put-handler"

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
    const fullTopic = `${config.mqtt.topic}/${topic}`

    if (!client) {
        log.error(`MQTT not available, cannot publish to ${fullTopic}`)
        return
    }

    const body = JSON.stringify(message, (key, value) => {
        if (value !== null) return value
    })

    client.publish(fullTopic, body, {retain: true})
}

export const connectMqtt = () => {
    const options = {
        clean: true,
        connectTimeout: 4000,
        clientId: makeid(9),
        // TODO Auth
        // username: 'emqx_test',
        // password: 'emqx_test',
    }

    client = mqtt.connect(config.mqtt.url, options)
    client.on("connect", function () {
        log.info("MQTT Connected")
        client.subscribe(`${config.mqtt.topic}/#`, (err) => {
            if (!err) {
                log.info("MQTT subscription active")
            }
            else {
                log.error(err)
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
}
