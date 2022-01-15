import { HueEvent, HueEventData } from "../api/v2/types/event"
import { fromLight } from "../messages/light-message"
import { log } from "../logger"
import { getTopic, state } from "./state-manager"
import { HueIdentifiable } from "../api/v2/types/general"
import { isLight } from "../api/v2/types/light"
import { isButton } from "../api/v2/types/button"
import { fromButton } from "../messages/button-message"

const handleResource = (data: HueEventData) => {
    const oldResource = state._typedResources.get(data.id)
    if (oldResource) {
        const newResource = {...oldResource, ...data} as HueIdentifiable

        state._typedResources.set(data.id, newResource)

        if (isLight(newResource)) {
            console.log(getTopic(newResource), fromLight(newResource))
        }
        else if (isButton(newResource)) {
            console.log(getTopic(newResource), fromButton(newResource))
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
            case "button":
                handleResource(data)
                break
            case "motion":
                break
            case "temperature":
                break
            default:
                console.log(`Unhandled device: ${data.type}`)
        }
    }
}
