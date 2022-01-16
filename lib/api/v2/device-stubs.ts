import { Room } from "./types/room"
import { Device } from "./types/device"
import { Light } from "./types/light"
import { Button } from "./types/button"

export const deviceStubs = {
    room: {
        id: "id",
        id_v1: "id",
        metadata: { name: "my-room", archetype: "room" },
        services: [],
        children: [],
        type: "room"
    },
    zgp: {
        "id": "0b3eadd9-a595-4839-91f7-52d9e1667667",
        "id_v1": "/sensors/29",
        "owner": {
            "rid": "a2409be6-2307-4975-aa2a-3908c145140d",
            "rtype": "device"
        },
        "source_id": "00:00:00:00:01:02:03:04",
        "status": "unidirectional_incoming",
        "type": "zgp_connectivity"
    },

    zigbee: {
        "id": "b246cf7a-962a-4c3e-86e4-26082e3746dd",
        "id_v1": "",
        "mac_address": "00:01:02:03:04:05:06:07",
        "owner": {
            "rid": "2d98dacf-d896-4fb9-b338-62d6e2ffb546",
            "rtype": "device"
        },
        "status": "connected",
        "type": "zigbee_connectivity"
    },

    temperature: {
        "enabled": true,
        "id": "dba4c348-8d92-4690-8dd2-bdb3730dde90",
        "id_v1": "/sensors/109",
        "owner": {
            "rid": "69216f7e-5568-44a2-8305-d12202083e86",
            "rtype": "device"
        },
        "temperature": {
            "temperature": 21.3700008392334,
            "temperature_valid": true
        },
        "type": "temperature"
    },

    devicePower: {
        "id": "5655bfa1-149e-4115-876e-a368e626609e",
        "id_v1": "/sensors/94",
        "owner": {
            "rid": "93b0532d-37a7-489e-84d7-741735815edb",
            "rtype": "device"
        },
        "power_state": { "battery_level": 100, "battery_state": "normal" },
        "type": "device_power"
    },

    lightLevel: {
        "enabled": true,
        "id": "ec374650-a289-4882-9084-406dabf80c97",
        "id_v1": "/sensors/108",
        "light": { "light_level": 22230, "light_level_valid": true },
        "owner": {
            "rid": "9b70fbd7-7da5-43ed-908b-f9ad57f26287",
            "rtype": "device"
        },
        "type": "light_level"
    },

    motion: {
        "enabled": true,
        "id": "41753df9-0ada-47ad-9ac2-4fc9ed08faf6",
        "id_v1": "/sensors/107",
        "motion": { "motion": true, "motion_valid": true },
        "owner": {
            "rid": "af7dfde6-b4df-4281-b095-8417d897c8e2",
            "rtype": "device"
        },
        "type": "motion"
    },

    buttonDevice: {
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
    },

    button: {
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
    },

    light: {
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
}
