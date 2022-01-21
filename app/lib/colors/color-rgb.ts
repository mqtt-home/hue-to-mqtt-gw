export const convertToRGB = (x: number, y: number, brightness: number) => {
    const _x = x
    const _y = y
    const z = 1.0 - _x - _y
    const Y = brightness
    const X = (Y / _y) * _x
    const Z = (Y / _y) * z

    let r = X * 1.656492 - Y * 0.354851 - Z * 0.255038
    let g = -X * 0.707196 + Y * 1.655397 + Z * 0.036152
    let b = X * 0.051713 - Y * 0.121364 + Z * 1.011530

    r = r <= 0.0031308 ? 12.92 * r : (1.0 + 0.055) * Math.pow(r, (1.0 / 2.4)) - 0.055
    g = g <= 0.0031308 ? 12.92 * g : (1.0 + 0.055) * Math.pow(g, (1.0 / 2.4)) - 0.055
    b = b <= 0.0031308 ? 12.92 * b : (1.0 + 0.055) * Math.pow(b, (1.0 / 2.4)) - 0.055

    if (r > b && r > g) {
        // red is biggest
        if (r > 1.0) {
            g = g / r
            b = b / r
            r = 1.0
        }
    }
    else if (g > b && g > r) {
        // green is biggest
        if (g > 1.0) {
            r = r / g
            b = b / g
            g = 1.0
        }
    }
    else if (b > r && b > g) {
        // blue is biggest
        if (b > 1.0) {
            r = r / b
            g = g / b
            b = 1.0
        }
    }

    return {
        r: Math.round(r * 255),
        g: Math.round(g * 255),
        b: Math.round(b * 255)
    }
}
