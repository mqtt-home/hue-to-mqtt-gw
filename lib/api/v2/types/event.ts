import { HueIdentifiable, HueOwnable, ResourceType } from "./general"
import { MotionData } from "./motion"
import { ButtonData } from "./button"
import { LightColorData, LightColorTemperatureData, LightDimmingData, LightOnOffData } from "./light"
import { TemperatureData } from "./temperature"

/* eslint-disable camelcase */
export type HueEventData = HueIdentifiable & HueOwnable & {
    motion?: MotionData
    button?: ButtonData
    on?: LightOnOffData
    dimming?: LightDimmingData
    color_temperature?: LightColorTemperatureData
    color?: LightColorData
    temperature?: TemperatureData
}

export type HueEvent = {
    creationtime: string
    data: HueEventData[]
    id: string
    type: ResourceType
}
