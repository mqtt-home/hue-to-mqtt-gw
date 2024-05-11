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
import { publish } from "../mqtt/mqtt-client"
import { isGroupedLight } from "../api/v2/types/grouped-light"
import { fromGroupedLight } from "../messages/grouped-light-message"

const handleResource = (data: HueEventData) => {
    const oldResource = state._typedResources.get(data.id)
    if (oldResource) {
        const newResource = { ...oldResource, ...data } as HueIdentifiable

        state._typedResources.set(data.id, newResource)

        publishResource(newResource)
    }
    else {
        log.warn(`No resource found with id ${data.id}`)
    }
}

export const publishResource = (resource: HueIdentifiable) => {
    let message: any
    let topic = getTopic(resource)

    if (isLight(resource)) {
        message = fromLight(resource)
    }
    else if (isLightLevel(resource)) {
        message = fromLightLevel(resource)
    }
    else if (isButton(resource)) {
        message = fromButton(resource)
    }
    else if (isMotion(resource)) {
        message = fromMotion(resource)
    }
    else if (isTemperature(resource)) {
        message = fromTemperature(resource)
    }
    else if (isDevicePower(resource)) {
        message = fromDevicePower(resource)
    }
    else if (isZigbeeGreenPowerConnectivity(resource)) {
        message = fromZgpConnectivity(resource)
    }
    else if (isZigbeeConnectivity(resource)) {
        message = fromZigbeeConnectivity(resource)
    }
    else if (isGroupedLight(resource)) {
        message = fromGroupedLight(resource)
    }
    else {
        topic = `unhandled/${topic}`
        message = resource
    }

    publish(message, topic)
}

export const takeEvent = (event: HueEvent) => {
    for (const data of event.data) {
        handleResource(data)
    }
}
