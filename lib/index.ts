import { log } from "./logger"

import cron from "node-cron"
import { initStateManagerFromHue } from "./state/state-manager"
import { startSSE } from "./SSEClient"
import { takeEvent } from "./state/state-event-handler"
import { connectMqtt } from "./mqtt/mqtt-client"
import { loadConfig } from "./config/config"
import path from "path"

export const triggerFullUpdate = async () => {
    log.info("Updating devices")
    await initStateManagerFromHue()
    log.info("Updating devices done")
}

if (process.argv.length !== 3) {
    log.error("Expected config file as argument.")
    process.exit(1)
}

let configFile = process.argv[2]
configFile = configFile.startsWith(".") ? path.join(__dirname, "..", configFile) : configFile
log.info(`Using config from file ${configFile}`)
loadConfig(configFile)

connectMqtt().then(() => {
    triggerFullUpdate().then(async () => {
        const sse = startSSE()
        sse.addEventListener("message", event => {
            for (const data of JSON.parse(event.data)) {
                takeEvent(data)
            }
        })

        log.info("Application is now ready.")

        cron.schedule("0 * * * *", triggerFullUpdate).start()
    })
})
