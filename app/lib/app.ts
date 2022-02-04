import { connectMqtt } from "./mqtt/mqtt-client"
import { startSSE } from "./SSEClient"
import { takeEvent } from "./state/state-event-handler"
import { log } from "./logger"
import cron from "node-cron"
import { initStateManagerFromHue } from "./state/state-manager"
import { getAppConfig } from "./config/config"

export const triggerFullUpdate = async () => {
    log.info("Updating devices")
    await initStateManagerFromHue()
    log.info("Updating devices done")
}

export const startApp = async () => {
    const mqttCleanUp = await connectMqtt()
    await triggerFullUpdate()

    const sse = startSSE()
    sse.addEventListener("message", event => {
        for (const data of JSON.parse(event.data)) {
            takeEvent(data)
        }
    })

    log.info("Application is now ready.")

    let task: cron.ScheduledTask | null
    if (getAppConfig()["hourly-full-update"]) {
        log.info("Scheduling hourly-full-update.")
        task = cron.schedule("0 * * * *", triggerFullUpdate)
        task.start()
    }

    return () => {
        mqttCleanUp()
        sse.close()
        task?.stop()
    }
}
