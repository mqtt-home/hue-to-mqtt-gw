import { getReplacement, nextIdv1 } from "./replacer"
import { randomUUID } from "crypto"
import { makeMac, makeName } from "./id-generator"

export const transform: (fragment: any) => any  = (fragment: any) => {
    if (fragment === null || fragment === undefined || typeof fragment === "string" || typeof fragment === "number") {
        return fragment
    }
    else if (Array.isArray(fragment)) {
        return (fragment as any[]).map(child => transform(child))
    }

    let result = {...fragment}

    for (const [key, value] of Object.entries(fragment)) {
        if (typeof value == "object") {
            result[key] = transform(value)
        }
        else if (typeof value == "string") {
            if (key === "id" || key === "rid" || key === "bridge_id" ) {
                result[key] = getReplacement(value, randomUUID)
            }
            else if (key === "id_v1") {
                result[key] = getReplacement(value, () => `idv1/${nextIdv1()}`)
            }
            else if (key === "name") {
                result[key] = getReplacement(value, () => makeName(4))
            }
            else if (key === "mac_address" || key === "source_id") {
                result[key] = getReplacement(value, makeMac)
            }
        }
    }
    return result
}
