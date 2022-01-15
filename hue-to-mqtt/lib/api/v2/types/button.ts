import { HueResource, Resource } from "./general"

export type ButtonEvent = "initial_press"|"repeat"|"short_release"|"long_release"|"double_short_release"

export type Button = HueResource & {
    type?: "button"
    metadata: {control_id: 0|1|2|3|4|5|6|7|8}
    button: {last_event: ButtonEvent}
}
