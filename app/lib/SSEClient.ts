import EventSource from "eventsource"
import { log } from "./logger"
import { getAppConfig } from "./config/config"
import { takeEvent } from "./state/state-event-handler"

let sse: EventSource | null = null
let lastEvent = Date.now()

const startSSE = () => {
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
        log.error("SSE Error", err)
    }
    return sse
}

const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

const registerSSEWatchDog = () => {
    const millis = getAppConfig().hue["sse-watchdog-millis"]
    if (millis === 0) {
        log.info("SSE watchdog disabled")
        return
    }

    lastEvent = Date.now()
    log.info(`SSE watchdog enabled with ${millis}ms`)
    setInterval(() => {
        log.debug("Checking for SSE watchdog", Date.now() - lastEvent, millis)
        if (Date.now() - lastEvent > millis) {
            log.error("SSE watchdog triggered")
            sse?.close()
            sse = startSSE()
        }
    }, millis / 2)
}

export const initSSE = () => {
    sse = startSSE()
    sse.addEventListener("message", event => {
        lastEvent = Date.now()
        for (const data of JSON.parse(event.data)) {
            takeEvent(data)
        }
    })

    sse.onerror = async (err: any) => {
        log.error("SSE Error", err)
        await sleep(5000)
        log.info("Reconnecting to SSE")
        sse = startSSE()
    }

    registerSSEWatchDog()
}

export const destroySSE = () => {
    if (sse) {
        sse.close()
    }
}
