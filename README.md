# hue-to-mqtt

[![mqtt-smarthome](https://img.shields.io/badge/mqtt-smarthome-blue.svg)](https://github.com/mqtt-smarthome/mqtt-smarthome)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=de.rnd7.huemqtt%3Ade.rnd7.huemqtt.parent&metric=alert_status)](https://sonarcloud.io/dashboard?id=de.rnd7.huemqtt%3Ade.rnd7.huemqtt.parent)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=de.rnd7.huemqtt%3Ade.rnd7.huemqtt.parent&metric=coverage)](https://sonarcloud.io/dashboard?id=de.rnd7.huemqtt%3Ade.rnd7.huemqtt.parent)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=de.rnd7.huemqtt%3Ade.rnd7.huemqtt.parent&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=de.rnd7.huemqtt%3Ade.rnd7.huemqtt.parent)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=de.rnd7.huemqtt%3Ade.rnd7.huemqtt.parent&metric=security_rating)](https://sonarcloud.io/dashboard?id=de.rnd7.huemqtt%3Ade.rnd7.huemqtt.parent)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=de.rnd7.huemqtt%3Ade.rnd7.huemqtt.parent&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=de.rnd7.huemqtt%3Ade.rnd7.huemqtt.parent)

Convert Philips Hue messages to mqtt messages.

# Why another hue-mqtt-bridge?

- Support for `Friends of Hue` switches.
- I like to switch to `zigbee2mqtt` for my hue lights at some
point of time. The light messages are compatible with this format.

## Supported devices

- Lights
- Philips Switches
- Friends of Hue Switches
- Temperature sensors
- Presence sensors
- Daylight sensors
- Ambient light sensors

## Example message

The message will be posted to the following topic: `hue/light/room/some-light`

```json
{
    "state":"ON",
    "brightness":254,
    "color_temp":230
}
```

Post a message
```json
{
    "state":"OFF"
}
```

to the topic `hue/light/room/some-light/set` to turn the light off.

Post a message
```json
{
    "state":"ON",
    "brightness":254,
    "color_temp":230
}
```
to the topic `hue/light/room/some-light/set` to turn the light on with the given brightness and color_temp.

### Color

```json
{
    "state":"ON",
    "brightness":254,
    "color":{"x":0.3691,"y":0.3719}
}
```

### Get sate

Post a message `{"state": ""}` to `hue/light/room/some-light/get` to get the current light state.

### Switch

```json
{
    "button":1,
    "code":1000,
    "last-updated":"2021-01-09T13:08:17Z[UTC]"
}
```

## Example configuration

```json
{
    "mqtt": {
        "url": "tcp://192.168.2.98:1883",
        "username": "user",
        "password": "password"
    },
    "hue": {
        "host": "192.168.2.99",
        "api-key": "api-key-here"
    }
}
```

# Bridge status

The bridge maintains two status topics:

## Topic: `.../bridge/state`

| Value     | Description                          |
| --------- | ------------------------------------ |
| `online`  | The bridge is started                |
| `offline` | The bridge is currently not started. |

# Docker

This application is intended to be executed using docker. Example docker compose usage:

```
huemqtt:
  hostname: huemqtt
  image: pharndt/hue2mqtt:1.0.2
  volumes:
    - ./config/huemqtt:/var/lib/huemqtt:ro
  restart: always 
  depends_on:
   - mosquitto
```
