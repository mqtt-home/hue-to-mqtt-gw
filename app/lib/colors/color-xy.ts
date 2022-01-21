import { GamutType } from "../api/v2/types/light"

// Original source https://github.com/Q42Philips/hue-color-converter
// Converted to TypeScript

const colorTriangle = (gamutType: GamutType) => {
    switch (gamutType) {
    case "A":
        return [[0.703, 0.296], [0.214, 0.709], [0.139, 0.081]]
    case "B":
        return [[0.674, 0.322], [0.408, 0.517], [0.168, 0.041]]
    case "C":
        return [[0.692, 0.308], [0.17, 0.7], [0.153, 0.048]]
    default:
        return [[1.0, 0.0], [0.0, 1.0], [0.0, 0.0]]
    }
}

const checkPointInLampsReach = (point: number[], colorPoints: number[][]) => {
    if (point != null && colorPoints != null) {
        const red = colorPoints[0]
        const green = colorPoints[1]
        const blue = colorPoints[2]
        const v1 = [green[0] - red[0], green[1] - red[1]]
        const v2 = [blue[0] - red[0], blue[1] - red[1]]
        const q = [point[0] - red[0], point[1] - red[1]]
        const s = crossProduct(q, v2) / crossProduct(v1, v2)
        const t = crossProduct(v1, q) / crossProduct(v1, v2)
        return s >= 0.0 && t >= 0.0 && s + t <= 1.0
    }
    else {
        return false
    }
}

const crossProduct = (point1: number[], point2: number[]) => {
    return point1[0] * point2[1] - point1[1] * point2[0]
}

const getClosestPointToPoints = (pointA: number[], pointB: number[], pointP: number[]) => {
    const pointAP = [pointP[0] - pointA[0], pointP[1] - pointA[1]]
    const pointAB = [pointB[0] - pointA[0], pointB[1] - pointA[1]]
    const ab2 = pointAB[0] * pointAB[0] + pointAB[1] * pointAB[1]
    const apAb = pointAP[0] * pointAB[0] + pointAP[1] * pointAB[1]
    let t = apAb / ab2
    if (t < 0.0) {
        t = 0.0
    }
    else if (t > 1.0) {
        t = 1.0
    }

    return [pointA[0] + pointAB[0] * t, pointA[1] + pointAB[1] * t]
}

const getDistanceBetweenTwoPoints = (pointA: number[], pointB: number[]) => {
    const dx = pointA[0] - pointB[0]
    const dy = pointA[1] - pointB[1]
    return Math.sqrt(dx * dx + dy * dy)
}

const precision = (d: number) => {
    return Math.round(10000.0 * d) / 10000.0
}

export const convertToXY = (red: number, green: number, blue: number, gamutType: GamutType) => {
    const _red = red / 255
    const _green = green / 255
    const _blue = blue / 255
    const r = _red > 0.04045 ? Math.pow(((_red + 0.055) / 1.055), 2.4000000953674316) : _red / 12.92
    const g = _green > 0.04045 ? Math.pow(((_green + 0.055) / 1.055), 2.4000000953674316) : _green / 12.92
    const b = _blue > 0.04045 ? Math.pow(((_blue + 0.055) / 1.055), 2.4000000953674316) : _blue / 12.92
    const x = r * 0.664511 + g * 0.154324 + b * 0.162028
    const y = r * 0.283881 + g * 0.668433 + b * 0.047685
    const z = r * 8.8E-5 + g * 0.07231 + b * 0.986039
    const xy = [x / (x + y + z), y / (x + y + z)]
    if (isNaN(xy[0])) {
        xy[0] = 0.0
    }

    if (isNaN(xy[1])) {
        xy[1] = 0.0
    }

    const colorPoints = colorTriangle(gamutType)
    const inReachOfLamps = checkPointInLampsReach(xy, colorPoints)
    if (!inReachOfLamps) {
        const pAB = getClosestPointToPoints(colorPoints[0], colorPoints[1], xy)
        const pAC = getClosestPointToPoints(colorPoints[2], colorPoints[0], xy)
        const pBC = getClosestPointToPoints(colorPoints[1], colorPoints[2], xy)
        const dAB = getDistanceBetweenTwoPoints(xy, pAB)
        const dAC = getDistanceBetweenTwoPoints(xy, pAC)
        const dBC = getDistanceBetweenTwoPoints(xy, pBC)
        let lowest = dAB
        let closestPoint = pAB
        if (dAC < dAB) {
            lowest = dAC
            closestPoint = pAC
        }

        if (dBC < lowest) {
            closestPoint = pBC
        }

        xy[0] = closestPoint[0]
        xy[1] = closestPoint[1]
    }

    return { x: precision(xy[0]), y: precision(xy[1])}
}
