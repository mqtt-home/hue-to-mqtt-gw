import EventSource from "eventsource"
import { log } from "./logger"
import { getAppConfig } from "./config/config"

export const startSSE = () => {
    log.info("Starting Server-Sent events")

    const config = getAppConfig()
    const eventSourceInitDict = {
        headers: {
            "hue-application-key": config.hue["api-key"],
            Accept: "text/event-stream"
        },
        https: { rejectUnauthorized: false }
    }

    const baserUrl = `https://${config.hue.host}:${config.hue.port}`
    const sse = new EventSource(`${baserUrl}/eventstream/clip/v2`, eventSourceInitDict)
    sse.onerror = (err: any) => {
        if (err) {
            log.error("SSE Error", err)
        }
    }
    return sse
}
