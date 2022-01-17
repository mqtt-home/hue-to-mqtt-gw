import config from "../../production/config/config.json"
import { applyConfig } from "./config/config"
import { loadTyped } from "./api/v2/hue-api-v2"
import { randomUUID } from "crypto"
import * as fs from "fs"

applyConfig(config)

describe("Test data", () => {

    let idCtr = 0
    let idv1Ctr = 0
    const map = new Map()

    const makeName = (length: number) => {
        let result = ""
        const rooms = [
            "Living Room",
            "Bedroom",
            "Kitchen",
            "Dining Room",
            "Family Room",
            "Guest Room",
            "Bathroom",
            "Game Room",
            "Basement",
            "Home Office",
            "Library",
            "Playroom",
            "Gym Room",
            "Storage Room"
        ]

        const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        const charactersLength = characters.length
        for (let i = 0; i < length; i++) {
            result += characters.charAt(Math.floor(Math.random() *
                charactersLength))
        }
        return rooms[Math.floor(Math.random() * rooms.length)] + " (" + result + ")"
    }

    const makeMac = () => {
        let result = ""
        const characters = "0123456789"
        const charactersLength = characters.length
        for (let i = 0; i < 6; i++) {
            result += characters.charAt(Math.floor(Math.random() *
                charactersLength))
            result += characters.charAt(Math.floor(Math.random() *
                charactersLength))
            result += "-"
        }
        return result.slice(0, result.length - 1)
    }

    const getReplacement = (value: string, provider: () => string) => {
        let replaceWith = map.get(value)
        if (!replaceWith) {
            replaceWith = provider()
            map.set(value, replaceWith)
        }
        return replaceWith
    }

    const traverse: (fragment: any) => any  = (fragment: any) => {
        if (fragment === null || fragment === undefined || typeof fragment === "string" || typeof fragment === "number") {
            return fragment
        }
        else if (Array.isArray(fragment)) {
            return (fragment as any[]).map(child => traverse(child))
        }

        let result = {...fragment}

        for (const [key, value] of Object.entries(fragment)) {
            if (typeof value == "object") {
                result[key] = traverse(value)
            }
            else if (typeof value == "string") {
                if (key === "id" || key === "rid") {
                    result[key] = getReplacement(value, randomUUID)
                }
                else if (key === "id_v1") {
                    result[key] = getReplacement(value, () => `idv1/${idv1Ctr++}`)
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

    test("g", () => {
        console.log(JSON.stringify(traverse({"status_values": ["none", "dynamic_palette"]})))
    })

    test("generate", async () => {
        fs.mkdirSync("./stubs")
        for (const typeName of ["device", "room", "light", "light_level", "bridge_home",
            "grouped_light", "bridge", "device_power", "zigbee_connectivity", "zgp_connectivity",
            "temperature", "motion", "button"]) {
            const resources = await loadTyped(typeName)
            if (resources) {
                fs.writeFileSync(`./stubs/${typeName}.json`, JSON.stringify(traverse(resources.data), null, 2))
            }
        }

        console.log("done")
    })
})
