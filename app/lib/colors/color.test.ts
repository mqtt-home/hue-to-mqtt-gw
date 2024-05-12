import { colors } from "."
import { describe, expect, test } from "vitest"

describe("Colors", () => {
    test("XY to RGB", async () => {
        expect(colors.toRGB(0.6758, 0.2953, 1))
            .toStrictEqual({ r: 255, g: 33, b: 51 })
        expect(colors.toRGB(0.2004, 0.5948, 1))
            .toStrictEqual({ r: 82, b: 116, g: 255 })
    })

    test("RGB to XY", async () => {
        expect(colors.toXY(255, 33, 51, "C"))
            .toStrictEqual({ x: 0.67, y: 0.2974 })

        expect(colors.toXY(82, 255, 116, "C"))
            .toStrictEqual({ x: 0.2016, y: 0.5919 })
    })
})
