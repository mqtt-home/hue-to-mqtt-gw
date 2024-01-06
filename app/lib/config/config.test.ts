import { applyDefaults, getAppConfig, loadConfig } from "./config"
import * as fs from "fs"

describe("Config", () => {
    test("default values", async () => {
        const config = {
            mqtt: {
                url: "tcp://192.168.1.1:1883",
                topic: "hue"
            },
            hue: {
                host: "192.168.1.1",
                "api-key": "some-api-key"
            }
        }

        expect(applyDefaults(config)).toStrictEqual({
            hue: {
                "api-key": "some-api-key",
                host: "192.168.1.1",
                port: 443,
                protocol: "https"
            },
            mqtt: {
                "bridge-info": true,
                qos: 1,
                retain: true,
                topic: "hue",
                url: "tcp://192.168.1.1:1883"
            },
            names: {},
            "send-full-update": true,
            loglevel: "info"
        })

        expect(applyDefaults(config)["send-full-update"]).toBeTruthy()
    })

    test("disable send-full-update", async () => {
        const config = {
            mqtt: {
                url: "tcp://192.168.1.1:1883",
                topic: "hue"
            },
            hue: {
                host: "192.168.1.1",
                "api-key": "some-api-key"
            },
            "send-full-update": false
        }

        expect(applyDefaults(config)["send-full-update"]).toBeFalsy()
    })

    test("load config with env variables", async () => {
        // new temp file
        const config = {
            mqtt: {
                url: "tcp://192.168.1.1:1883",
                topic: "hue"
            },
            hue: {
                host: "192.168.1.1",
                "api-key": "${API_KEY}"
            }
        }
        process.env.API_KEY = "12345"

        fs.writeFileSync("/tmp/config.json", JSON.stringify(config))
        loadConfig("/tmp/config.json")
        expect(getAppConfig().hue["api-key"]).toBe("12345")
    })
})
