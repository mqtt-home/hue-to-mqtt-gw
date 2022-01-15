import { add } from "./index"

describe("Calculator", () => {
    test("add", async () => {
        expect(add(4, 3)).toBe(7)
    })
})
