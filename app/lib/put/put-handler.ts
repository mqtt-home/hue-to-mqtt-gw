import { HueIdentifiable } from "../api/v2/types/general"
import { isLight } from "../api/v2/types/light"
import { isEffectMessage, LightEffectMessage, LightMessage, toGroupedLight, toLight } from "../messages/light-message"
import { putGroupedLightResource, putLightResource } from "../api/v2/hue-api-v2"
import { log } from "../logger"
import { applyEffect } from "./effects/light-effect-handler"
import { isGroupedLight } from "../api/v2/types/grouped-light"

export const putMessage = async (resource: HueIdentifiable, message: Buffer) => {
    if (isLight(resource)) {
        // only supported for light messages at the moment
        try {
            const lightMsg = JSON.parse(message.toString()) as LightMessage | LightEffectMessage
            if (isEffectMessage(lightMsg)) {
                await applyEffect(resource, lightMsg)
            }
            else {
                const newResource = toLight(resource, lightMsg)

                if (newResource.color && newResource.color_temperature) {
                    log.warn("PUT Handler: Both color and color_temperature set, put is likely to fail", {
                        newResource,
                        message
                    })
                }

                // resource will be updated by the Hue SSE API
                try {
                    await putLightResource(newResource)
                }
                catch (e) {
                    log.error(e + "\nMessage was:\n" + JSON.stringify(lightMsg, null, 2))
                }
            }
        }
        catch (e) {
            log.error("invalid message", e)
        }
    }
    else if (isGroupedLight(resource)) {
        const lightMsg = JSON.parse(message.toString()) as LightMessage
        const newResource = toGroupedLight(resource, lightMsg)

        // resource will be updated by the Hue SSE API
        try {
            await putGroupedLightResource(newResource)
        }
        catch (e) {
            log.error(e + "\nMessage was:\n" + JSON.stringify(lightMsg, null, 2))
        }
    }
}
