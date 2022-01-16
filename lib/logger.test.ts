import { createLogger, log } from "./logger"
import { Writable } from "stream"
import * as winston from "winston"

describe("Log format", () => {
    let output = ""
    let logger: winston.Logger

    beforeEach(() => {
        log.silent = true

        const stream = new Writable()
        stream._write = (chunk, encoding, next) => {
            output = output += chunk.toString()
            next()
        }
        logger = createLogger(new winston.transports.Stream({ stream }))
    })

    afterEach(() => {
        output = ""
    })

    test("info log", () => {
        logger.info("some info")

        expect(output).toMatch(/\d+\d+\d+\d+-\d+\d+-\d+\d+T.* INFO some info.*/)
    })

    test("warn log", () => {
        logger.warn("some warning")

        expect(output).toMatch(/\d+\d+\d+\d+-\d+\d+-\d+\d+T.* WARN some warning.*/)
    })

    test("error log", () => {
        logger.error("some error")

        expect(output).toMatch(/\d+\d+\d+\d+-\d+\d+-\d+\d+T.* ERROR some error.*/)
    })
})
