import { GroupedLight } from "../api/v2/types/grouped-light"

export type GroupedLightMessage = {
    state: "ON"|"OFF"
}

export const fromGroupedLight = (group: GroupedLight) => {
    return {
        state: group.on.on ? "ON" : "OFF"
    } as GroupedLightMessage
}
