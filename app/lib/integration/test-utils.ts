export const JEST_CONTAINER_TIMEOUT = 60 * 5 * 1000
export const JEST_DEFAULT_TIMEOUT = 5 * 1000

export const curlHealthTest = (host: string, port: number) => {
    return {
        test: `curl -f http://${host}:${port} || exit 1`,
        interval: 1000,
        timeout: 3000,
        retries: 5,
        startPeriod: 1000
    }
}

export const waitFor = async (predicate: () => boolean) => {
    while (!predicate()) {
        await new Promise(resolve => setTimeout(resolve, 10))
    }
}
