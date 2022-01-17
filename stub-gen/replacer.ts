
let idv1Ctr = 0
const map = new Map()

export const nextIdv1 = () => {
    return idv1Ctr++
}

export const getReplacement = (value: string, provider: () => string) => {
    let replaceWith = map.get(value)
    if (!replaceWith) {
        replaceWith = provider()
        map.set(value, replaceWith)
    }
    return replaceWith
}
