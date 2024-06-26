import { connectMqtt } from "./mqtt/mqtt-client"
import { destroySSE, initSSE } from "./SSEClient"
import { log } from "./logger"
import cron from "node-cron"
import { initStateManagerFromHue, state } from "./state/state-manager"

export const triggerFullUpdate = async () => {
    log.info("Updating devices")
    await initStateManagerFromHue()
    log.info("Updating devices done", `${state._typedResources.size} resources`)
}

export const startApp = async () => {
    const mqttCleanUp = await connectMqtt()
    await triggerFullUpdate()

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
