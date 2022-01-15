import { Button } from "../api/v2/types/button"

export type ButtonMessage = {
    button: number
    code: number
    "last-updated": string
    event?: string
}

export const fromButton = (button: Button) => {
    return {
        button: button.metadata.control_id,
        code: 0,
        event: button.button?.last_event,
        "last-updated": new Date().toISOString()
    } as ButtonMessage
}
