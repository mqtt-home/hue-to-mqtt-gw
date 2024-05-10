import { connectMqtt } from "./mqtt/mqtt-client"
import { destroySSE, initSSE } from "./SSEClient"
import { log } from "./logger"
import cron from "node-cron"
import { initStateManagerFromHue } from "./state/state-manager"
import { loadAllResources } from "./api/v2/hue-api-v2"

export const triggerFullUpdate = async () => {
    log.info("Updating devices")
    await initStateManagerFromHue()
    log.info("Updating devices done")
}

export const startApp = async () => {
    const mqttCleanUp = await connectMqtt()
    await triggerFullUpdate()

    const level = log.level()
    if (level === "DEBUG") {
        const resources = await loadAllResources()
        log.debug("All resources", JSON.stringify(resources))
    }

    initSSE()

    log.info("Scheduling hourly-full-update.")
    const task = cron.schedule("0 * * * *", triggerFullUpdate)
    task.start()

    log.info("Application is now ready.")

    return () => {
        mqttCleanUp()
        destroySSE()
        task.stop()
    }
}
