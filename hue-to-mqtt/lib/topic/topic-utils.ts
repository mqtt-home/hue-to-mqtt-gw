export const cleanTopic = (topic: string) => {
    return topic.toLowerCase()
        .replace(/ /g, "-")
        .replace(/ä/g, "ae")
        .replace(/ö/g, "oe")
        .replace(/ü/g, "ue");
}
