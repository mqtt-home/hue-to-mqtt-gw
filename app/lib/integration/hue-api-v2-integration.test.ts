import path from "path"
import { GenericContainer, StartedTestContainer, Wait } from "testcontainers"
import { loadDevices, loadTyped } from "../api/v2/hue-api-v2"
import { applyDefaults, setTestConfig } from "../config/config"
import { Result } from "../api/v2/types/general"
import { Room } from "../api/v2/types/room"
import { state } from "../state/state-manager"
import { isLight } from "../api/v2/types/light"
import lightsJson from "../../../stub/stubs/clip/v2/resource/light.json"
import roomsJson from "../../../stub/stubs/clip/v2/resource/room.json"
import devicesJson from "../../../stub/stubs/clip/v2/resource/device.json"
import { startApp } from "../app"
import { log } from "../logger"
import { curlHealthTest, JEST_CONTAINER_TIMEOUT, JEST_DEFAULT_TIMEOUT } from "./test-utils"

jest.setTimeout(JEST_CONTAINER_TIMEOUT)

describe("API v2 - Integration", () => {
    let hue: StartedTestContainer
    let mqtt: StartedTestContainer

    beforeAll(async () => {
        log.silent = true

        const buildRoot = path.resolve(__dirname, "../../../stub")
        const hueContainer = await GenericContainer.fromDockerfile(buildRoot)
            .build()

        hue = await hueContainer
            .withExposedPorts(80)
            .start()

        const mqttContainer = await GenericContainer.fromDockerfile(path.resolve(buildRoot, "activemq"))
            .build()

        mqtt = await mqttContainer
            .withExposedPorts(1883, 8161)
            .withHealthCheck(curlHealthTest("localhost", 8161))
            .withWaitStrategy(Wait.forHealthCheck())
            .start()

        setTestConfig(applyDefaults({
            hue: {
                port: hue.getMappedPort(80),
                host: hue.getHost(),
                "api-key": "none",
                protocol: "http"
            },
            mqtt: {
                url: `tcp://${mqtt.getHost()}:${mqtt.getMappedPort(1883)}`,
                topic: "hue"
            }
        }))
    })

    afterAll(async () => {
        await hue.stop()
        await mqtt.stop()
        log.silent = false
        jest.setTimeout(JEST_DEFAULT_TIMEOUT)
    })

    test("leaks", () => {

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

    test("init start app", async () => {
        const cleanup = await startApp()
        const firstLight = lightsJson.data[0].id
        expect(isLight(state.deviceByDeviceId.get(firstLight))).toBeFalsy()
        expect(isLight(state.getTyped().get(firstLight))).toBeTruthy()
        cleanup()
    })
})
