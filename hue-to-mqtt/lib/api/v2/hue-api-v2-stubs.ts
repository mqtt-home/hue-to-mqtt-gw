import { Room } from "./types/room"
import { Light } from "./types/light"

export const roomStub: Room = {
    id: "id",
    id_v1: "id",
    metadata: {name: "my-room", archetype: "room"},
    services: [],
    children: [],
    type: "room"
}

export const lightStub: Light = {
    "alert": {
        "action_values": [
            "breathe"
        ]
    },
    "color": {
        "gamut": {
            "blue": {
                "x": 0.1532,
                "y": 0.0475
            },
            "green": {
                "x": 0.17,
                "y": 0.7
            },
            "red": {
                "x": 0.6915,
                "y": 0.3083
            }
        },
        "gamut_type": "C",
        "xy": {
            "x": 0.4575,
            "y": 0.4099
        }
    },
    "color_temperature": {
        "mirek": 366,
        "mirek_schema": {
            "mirek_maximum": 500,
            "mirek_minimum": 153
        },
        "mirek_valid": true
    },
    "dimming": {
        "brightness": 100,
        "min_dim_level": 0.20000000298023224
    },
    "dynamics": {
        "speed": 0,
        "speed_valid": false,
        "status": "none",
        "status_values": [
            "none",
            "dynamic_palette"
        ]
    },
    "effects": {
        "effect_values": [
            "no_effect",
            "candle",
            "fire"
        ],
        "status": "no_effect",
        "status_values": [
            "no_effect",
            "candle",
            "fire"
        ]
    },
    "id": "2f30e921-beed-4459-9aef-0343b448077d",
    "id_v1": "/lights/6",
    "metadata": {
        "archetype": "hue_centris",
        "name": "Essen Spot 4"
    },
    "mode": "normal",
    "on": {
        "on": true
    },
    "owner": {
        "rid": "0a8ecbda-54e4-401b-8be9-d9a9f1c630b0",
        "rtype": "device"
    },
    "type": "light"
}
