import { HueIdentifiable, HueOwnable } from "./general"

export type ButtonEvent = "initial_press"|"repeat"|"short_release"|"long_release"|"double_short_release"

export type ButtonData = {
    last_event: ButtonEvent
}

export function isButton(object: HueIdentifiable): object is Button {
    return object && object.type === "button"
}

export type Button = HueIdentifiable & HueOwnable & {
    type?: "button"
    metadata: {control_id: 0|1|2|3|4|5|6|7|8}
    button?: ButtonData // Deviation from API description. Optional as some of my devices do not return this
}
