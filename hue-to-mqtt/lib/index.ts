import { log } from "./logger"
import config from "./config.json"
import axios from "axios"
import * as https from "https"

import cron from "node-cron"
import { initStateManagerFromHue } from "./state/state-manager"
import { startSSE } from "./SSEClient"

const triggerFullUpdate = async () => {
    log.info("Updating devices")
    await initStateManagerFromHue()
    log.info("Updating devices done")
}

triggerFullUpdate().then(() => {
    startSSE()
    log.info("Application is now ready.")

    // cron.schedule("*/15 * * * * *", () => {
    //     log.info("You will see this message every /15 second")
    // }).start()

    cron.schedule("0 * * * *", triggerFullUpdate).start()
})


