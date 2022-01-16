import { Room } from "../api/v2/types/room"
import { Device } from "../api/v2/types/device"
import { Light } from "../api/v2/types/light"
import { Button } from "../api/v2/types/button"

export const roomStub: Room = {
    id: "id",
    id_v1: "id",
    metadata: { name: "my-room", archetype: "room" },
    services: [],
    children: [],
    type: "room"
}

export const buttonDevice: Device = {
    id: "bc22f451-987c-4358-b697-04fd93a02759",
    id_v1: "/sensors/29",
    metadata: {
        archetype: "unknown_archetype",
        name: "Küche Oben"
    },
    product_data: {
        certified: true,
        manufacturer_name: "PhilipsFoH",
        model_id: "FOHSWITCH",
        product_archetype: "unknown_archetype",
        product_name: "Friends of Hue Switch",
        software_version: "0.0.0"
    },
    services: [
        {
            rid: "08c7fff3-4841-4e23-929f-061087849890",
            rtype: "button"
        },
        {
            rid: "cbce2b20-ecc6-477e-9acf-4b3fe0196a97",
            rtype: "button"
        },
        {
            rid: "6f70a770-cdd4-4332-a03d-4ff59808c2ae",
            rtype: "button"
        },
        {
            rid: "b1f53638-4dd9-4ece-8a0f-dceda8562db0",
            rtype: "button"
        },
        {
            rid: "1d2c15c0-8bf6-4092-8f7e-a638ffaf1d45",
            rtype: "zgp_connectivity"
        }
    ],
    type: "device"
}

export const buttonStub: Button = {
    id: "08c7fff3-4841-4e23-929f-061087849890",
    id_v1: "/sensors/29",
    metadata: {
        control_id: 1
    },
    owner: {
        rid: "bc22f451-987c-4358-b697-04fd93a02759",
        rtype: "device"
    },
    type: "button"
}

export const lightStub: Light = {
    alert: {
        action_values: [
            "breathe"
        ]
    },
    color: {
        gamut: {
            blue: {
                x: 0.1532,
                y: 0.0475
            },
            green: {
                x: 0.17,
                y: 0.7
            },
            red: {
                x: 0.6915,
                y: 0.3083
            }
        },
        gamut_type: "C",
        xy: {
            x: 0.4575,
            y: 0.4099
        }
    },
    color_temperature: {
        mirek: 366,
        mirek_schema: {
            mirek_maximum: 500,
            mirek_minimum: 153
        },
        mirek_valid: true
    },
    dimming: {
        brightness: 100,
        min_dim_level: 0.20000000298023224
    },
    dynamics: {
        speed: 0,
        speed_valid: false,
        status: "none",
        status_values: [
            "none",
            "dynamic_palette"
        ]
    },
    effects: {
        effect_values: [
            "no_effect",
            "candle",
            "fire"
        ],
        status: "no_effect",
        status_values: [
            "no_effect",
            "candle",
            "fire"
        ]
    },
    id: "2f30e921-beed-4459-9aef-0343b448077d",
    id_v1: "/lights/6",
    metadata: {
        archetype: "hue_centris",
        name: "Essen Spot 4"
    },
    mode: "normal",
    on: {
        on: true
    },
    owner: {
        rid: "0a8ecbda-54e4-401b-8be9-d9a9f1c630b0",
        rtype: "device"
    },
    type: "light"
}
