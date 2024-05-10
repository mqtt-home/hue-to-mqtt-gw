import EventSource from "eventsource"
import { log } from "./logger"
import { getAppConfig } from "./config/config"
import { takeEvent } from "./state/state-event-handler"

let sse: EventSource | null = null
let lastEvent = Date.now()
let interval: any | null = null

const startSSE = () => {
    log.info("[SSE] Starting Server-Sent events")

    const config = getAppConfig()
    const eventSourceInitDict = {
        headers: {
            "hue-application-key": config.hue["api-key"],
            Accept: "text/event-stream"
        },
        https: { rejectUnauthorized: false }
    }

    const baserUrl = `https://${config.hue.host}:${config.hue.port}`
    return new EventSource(`${baserUrl}/eventstream/clip/v2`, eventSourceInitDict)
}

const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

const registerWatchDog = () => {
    const millis = getAppConfig().hue["sse-watchdog-millis"]
    if (millis === 0) {
        log.info("[SSE] Watchdog disabled")
        return
    }

    lastEvent = Date.now()
    log.info(`[SSE] Watchdog enabled with ${millis}ms`)
    interval = setInterval(() => {
        log.debug("[SSE] Checking watchdog", Date.now() - lastEvent, millis)
        if (Date.now() - lastEvent > millis) {
            log.error("[SSE] Watchdog triggered, resetting")
            initSSE()
        }
    }, millis / 2)
}

export const initSSE = () => {
    // clean old instance first
    destroySSE()

    sse = startSSE()
    sse.addEventListener("message", event => {
        lastEvent = Date.now()
        for (const data of JSON.parse(event.data)) {
            takeEvent(data)
        }
    })

    sse.onerror = async (err: any) => {
        log.error("[SSE] Error", err)
        await sleep(5000)
        log.info("[SSE] Reconnecting...")
        initSSE()
    }

    registerWatchDog()
}

export const destroySSE = () => {
    if (sse) {
        try {
            sse.close()
        }
        catch (e) {
            log.error("[SSE] Error while closing", e)
        }
        sse = null
    }
    if (interval) {
        try {
            clearInterval(interval)
        }
        catch (e) {
            log.error("[SSE] Error while clearing watchdog interval", e)
        }
        interval = null
    }
}
