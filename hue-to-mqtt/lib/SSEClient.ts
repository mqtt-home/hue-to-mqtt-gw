import EventSource from "eventsource"
import { log } from "./logger"
import config from "./config.json"

export const startSSE = () => {
    log.info("Starting Server-Sent events")

    let eventSourceInitDict = {
        headers: {
            "hue-application-key": config.hue["api-key"],
            "Accept": "text/event-stream"
        },
        https: {rejectUnauthorized: false}};

    let baserUrl = `https://${config.hue.host}:${config.hue.port}`
    const sse = new EventSource(`${baserUrl}/eventstream/clip/v2`, eventSourceInitDict);
    sse.onerror = (err: any) => {
        if (err) {
            log.error(err)
        }
    }

    sse.addEventListener("message", event => {
        log.info("New event")
        console.log(event)
    })
}

