import { HueIdentifiable, HueOwnable } from "./general"
import { isMotion } from "./motion"

export type ButtonEvent = "initial_press"|"repeat"|"short_release"|"long_release"|"double_short_release"

/* eslint-disable camelcase */
export type ButtonData = {
    last_event: ButtonEvent
}

/* eslint-disable camelcase */
export type Button = HueIdentifiable & HueOwnable & {
    type?: "button"
    metadata: {
        control_id: 0|1|2|3|4|5|6|7|8
    }
    button?: ButtonData // Deviation from API description. Optional as some of my devices do not return this
}

export function isButton (object: HueIdentifiable): object is Button {
    return object && object.type === "button"
}

export function isTrigger (object: HueIdentifiable): boolean {
    return isButton(object) || isMotion(object)
}
