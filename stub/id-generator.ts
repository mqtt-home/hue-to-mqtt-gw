export const makeName = (length: number) => {
    let result = ""
    const rooms = [
        "Living Room",
        "Bedroom",
        "Kitchen",
        "Dining Room",
        "Family Room",
        "Guest Room",
        "Bathroom",
        "Game Room",
        "Basement",
        "Home Office",
        "Library",
        "Playroom",
        "Gym Room",
        "Storage Room"
    ]

    const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    const charactersLength = characters.length
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() *
            charactersLength))
    }
    return rooms[Math.floor(Math.random() * rooms.length)] + " (" + result + ")"
}

export const makeMac = () => {
    let result = ""
    const characters = "0123456789"
    const charactersLength = characters.length
    for (let i = 0; i < 6; i++) {
        result += characters.charAt(Math.floor(Math.random() *
            charactersLength))
        result += characters.charAt(Math.floor(Math.random() *
            charactersLength))
        result += "-"
    }
    return result.slice(0, result.length - 1)
}
