import { HueEvent, HueEventData } from "../api/v2/types/event"
import { fromLight } from "../messages/light-message"
import { log } from "../logger"
import { getTopic, state } from "./state-manager"
import { HueIdentifiable } from "../api/v2/types/general"
import { isLight } from "../api/v2/types/light"
import { isButton } from "../api/v2/types/button"
import { fromButton } from "../messages/button-message"
import { isMotion } from "../api/v2/types/motion"
import { fromMotion } from "../messages/presence-message"
import { isTemperature } from "../api/v2/types/temperature"
import { fromTemperature } from "../messages/temperature-message"
import { isLightLevel } from "../api/v2/types/light-level"
import { fromLightLevel } from "../messages/ambient-message"
import { isDevicePower } from "../api/v2/types/device-power"
import { fromDevicePower } from "../messages/device-power-message"
import { isZigbeeGreenPowerConnectivity } from "../api/v2/types/zgp-connectivity"
import { fromZgpConnectivity } from "../messages/zgp-connectivity-message"
import { isZigbeeConnectivity } from "../api/v2/types/zigbee-connectivity"
import { fromZigbeeConnectivity } from "../messages/zigbee-connectivity-message"

const handleResource = (data: HueEventData) => {
    const oldResource = state._typedResources.get(data.id)
    if (oldResource) {
        const newResource = {...oldResource, ...data} as HueIdentifiable

        state._typedResources.set(data.id, newResource)

        if (isLight(newResource)) {
            console.log(getTopic(newResource), fromLight(newResource))
        }
        else if (isLightLevel(newResource)) {
            console.log(getTopic(newResource), fromLightLevel(newResource))
        }
        else if (isButton(newResource)) {
            console.log(getTopic(newResource), fromButton(newResource))
        }
        else if (isMotion(newResource)) {
            console.log(getTopic(newResource), fromMotion(newResource))
        }
        else if (isTemperature(newResource)) {
            console.log(getTopic(newResource), fromTemperature(newResource))
        }
        else if (isDevicePower(newResource)) {
            console.log(getTopic(newResource), fromDevicePower(newResource))
        }
        else if (isZigbeeGreenPowerConnectivity(newResource)) {
            console.log(getTopic(newResource), fromZgpConnectivity(newResource))
        }
        else if (isZigbeeConnectivity(newResource)) {
            console.log(getTopic(newResource), fromZigbeeConnectivity(newResource))
        }
    }
    else {
        log.error(`No resource found with id ${data.id}`)
    }
}

export const takeEvent = (event: HueEvent) => {
    for (const data of event.data) {
        switch (data.type) {
            case "light":
            case "light_level":
            case "button":
            case "motion":
            case "device_power":
            case "temperature":
                handleResource(data)
                break
            default:
                console.log(`Unhandled device: ${data.type}`)
        }
    }
}
