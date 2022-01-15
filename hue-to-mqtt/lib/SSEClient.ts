import EventSource from "eventsource"
import { log } from "./logger"
import config from "./config.json"

let eventSourceInitDict = {
    headers: {
        "hue-application-key": config.hue["api-key"],
        "Accept": "text/event-stream"
    },
    https: {rejectUnauthorized: false}};

let baserUrl = `https://${config.hue.host}:${config.hue.port}`
const sse = new EventSource(`${baserUrl}/eventstream/clip/v2`, eventSourceInitDict);
// sse.onerror = function (event) {
//     if (event.data instanceof Error) {
//         const err = event.data
//
//         if (err.status === 401 || err.status === 403) {
//             console.log('not authorized');
//         }
//         else {
//             console.log(err)
//         }
//     }
// };
sse.addEventListener("message", event => {
    log.info("New event")
    console.log(event)
})
