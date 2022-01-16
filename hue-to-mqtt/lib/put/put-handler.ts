import { HueIdentifiable } from "../api/v2/types/general"
import { isLight } from "../api/v2/types/light"
import { isEffectMessage, LightEffectMessage, LightMessage, toLight } from "../messages/light-message"
import { putResource } from "../api/v2/hue-api-v2"
import { log } from "../logger"
import { applyEffect } from "./effects/light-effect-handler"

export const putMessage = async (resource: HueIdentifiable, message: Buffer) => {
    if (isLight(resource)) {
        console.log("putMessage")
        // only supported for light messages at the moment
        const lightMsg = JSON.parse(message.toString()) as LightMessage | LightEffectMessage
        console.log(lightMsg)
        if (isEffectMessage(lightMsg)) {
            await applyEffect(resource, lightMsg)
        }
        else {
            console.log("no effect")
            const newResource = toLight(resource, lightMsg)

            // resource will be updated by the Hue SSE API
            try {
                await putResource(newResource)
            } catch (e) {
                log.error(e)
            }
        }
    }
}
