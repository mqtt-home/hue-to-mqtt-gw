import config from "../production/config/config.json"
import { applyConfig } from "../app/lib/config/config"
import { loadTyped } from "../app/lib/api/v2/hue-api-v2"

import * as fs from "fs"
import { transform } from "./transformator"

applyConfig(config)
const main = async () => {
    fs.mkdirSync("./stubs")
    for (const typeName of ["device", "room", "light", "light_level", "bridge_home",
        "grouped_light", "bridge", "device_power", "zigbee_connectivity", "zgp_connectivity",
        "temperature", "motion", "button"]) {
        const resources = await loadTyped(typeName)
        if (resources) {
            fs.writeFileSync(`./stubs/${typeName}.json`, JSON.stringify(transform(resources.data), null, 2))
        }
    }
}

main().then(() => console.log("done"))

