import { HueEvent, HueEventData } from "../api/v2/types/event"
import { Light } from "../api/v2/types/light"
import { fromLight } from "../messages/light-message"
import { log } from "../logger"
import { getTopic, state } from "./state-manager"
import { Button } from "../api/v2/types/button"
import { fromButton } from "../messages/button-message"

const handleLight = (data: HueEventData) => {
    const oldResource = state._lights.get(data.id)
    if (oldResource) {
        const newResource = {...oldResource, ...data} as Light

        state._lights.set(data.id, newResource)
        console.log(getTopic(newResource), fromLight(newResource))
    }
    else {
        log.error(`No resource found with id ${data.id}`)
    }
}

const handleButton = (data: HueEventData) => {
    const oldResource = state._buttons.get(data.id)
    if (oldResource) {
        const newResource = {...oldResource, ...data} as Button

        state._buttons.set(data.id, newResource)
        console.log(getTopic(newResource), fromButton(newResource))
    }
    else {
        log.error(`No resource found with id ${data.id}`)
    }
}

export const takeEvent = (event: HueEvent) => {
    for (const data of event.data) {
        switch (data.type) {
            case "light":
                handleLight(data)
                break
            case "button":
                handleButton(data)
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
