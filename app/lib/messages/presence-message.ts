import { Motion } from "../api/v2/types/motion"

export type PresenceMessage = {
    presence: boolean
    "last-updated": string
}

export const fromMotion = (motion: Motion) => {
    const message: PresenceMessage = {
        presence: motion.motion.motion,
        "last-updated": new Date().toISOString()
    }

    return message
}
