import { log } from "./logger"

import cron from "node-cron"
import { initStateManagerFromHue, state } from "./state/state-manager"
import { startSSE } from "./SSEClient"
import { takeEvent } from "./state/state-event-handler"

export const triggerFullUpdate = async () => {
    log.info("Updating devices")
    await initStateManagerFromHue()
    log.info("Updating devices done")
}

triggerFullUpdate().then(() => {
    const sse = startSSE()
    sse.addEventListener("message", event => {
        for (const data of JSON.parse(event.data)) {
            takeEvent(data)
        }
    })

    log.info("Application is now ready.")

    // cron.schedule("*/15 * * * * *", () => {
    //     log.info("You will see this message every /15 second")
    // }).start()

    cron.schedule("0 * * * *", triggerFullUpdate).start()
})
