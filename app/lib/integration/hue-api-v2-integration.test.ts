import path from "path"
import { GenericContainer, StartedTestContainer } from "testcontainers"
import { loadDevices, loadTyped } from "../api/v2/hue-api-v2"
import { applyDefaults, setTestConfig } from "../config/config"
import { Result } from "../api/v2/types/general"
import { Room } from "../api/v2/types/room"
import { initStateManagerFromHue, state } from "../state/state-manager"
import { isLight } from "../api/v2/types/light"
import lightsJson from "../../../stub/stubs/clip/v2/resource/light.json"
import roomsJson from "../../../stub/stubs/clip/v2/resource/room.json"
import devicesJson from "../../../stub/stubs/clip/v2/resource/device.json"

jest.setTimeout(60 * 5 * 1000)

describe("API v2 - Integration", () => {
    let hue: StartedTestContainer

    beforeAll(async () => {
        const buildContext = path.resolve(__dirname, "../../../stub")
        const container = await GenericContainer.fromDockerfile(buildContext)
            .build()

        hue = await container
            .withExposedPorts(80)
            .start()

        setTestConfig(applyDefaults({
            hue: {
                port: hue.getMappedPort(80),
                host: hue.getHost(),
                "api-key": "none",
                protocol: "http"
            },
            mqtt: {
                url: "tcp://localhost:1883",
                topic: "hue"
            }
        }))
    })

    afterAll(() => {
        hue.stop()
    })

    test("load devices", async () => {
        const devices = await loadDevices()
        expect(devices).toBeTruthy()
        expect(devices!.data.length).toBe(devicesJson.data.length)
    })

    test("load rooms", async () => {
        const rooms = (await loadTyped("room")) as Result<Room>
        expect(rooms.data.length).toBe(roomsJson.data.length)
    })

    test("init state manager", async () => {
        await initStateManagerFromHue()
        const firstLight = lightsJson.data[0].id
        expect(isLight(state.deviceByDeviceId.get(firstLight))).toBeFalsy()
        expect(isLight(state.getTyped().get(firstLight))).toBeTruthy()
    })
})
