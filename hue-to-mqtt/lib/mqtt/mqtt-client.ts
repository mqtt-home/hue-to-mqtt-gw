import mqtt from "mqtt"
import config from "../config.json"
import { log } from "../logger"

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

    client.publish(fullTopic, JSON.stringify(message), {retain: true})
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
                // client.publish('test', 'Hello mqtt')
            }
            else {
                log.error(err)
            }
        })
    })

    client.on("message",  (topic, message) => {
        log.info("MQTT subscription active")
        console.log(`MQTT Message received: ${topic}`)
    })
}
