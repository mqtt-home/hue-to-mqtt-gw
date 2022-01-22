import AsyncLock from "async-lock"

export const lock = new AsyncLock({ timeout: 5000 })

export const resolvable = () => {
    let resolveResult: any
    const promise = new Promise(resolve => {
        resolveResult = resolve
    })
    return [resolveResult, promise]
}
