# hue-to-mqtt

[![mqtt-smarthome](https://img.shields.io/badge/mqtt-smarthome-blue.svg)](https://github.com/mqtt-smarthome/mqtt-smarthome)

Convert Philips Hue messages to mqtt messages.

# Why another hue-mqtt-bridge?

- Support for `Friends of Hue` switches.
- I like to switch to `zigbee2mqtt` for my hue lights at some
point of time. The light messages are compatible with this format.
- Completely written using Hue API v2
- Server-Sent Events

# Docker

This application is intended to be executed using docker. Example docker compose usage:

```
huemqtt:
  hostname: huemqtt
  image: pharndt/hue2mqtt:2.5.1
  volumes:
    - ./config/huemqtt:/var/lib/huemqtt:ro
  restart: always 
  depends_on:
   - mosquitto
```

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

Note that unreachable lights are handled as they are turned off. 

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
    },
    "names": {
        "resource-uuid": "office"
    },
    "loglevel": "debug"
}
```

### Name mappings

for resources without an own name (like light groups) or when you like to define a custom
name, you can use the `names` configuration.

Without a name configuration, the name will be generated from
- the room (when available)
- the name defined by Hue (when available)
- the resource UUID as fallback 

# Bridge status

The bridge maintains two status topics:

## Topic: `.../bridge/state`

| Value     | Description                          |
| --------- | ------------------------------------ |
| `online`  | The bridge is started                |
| `offline` | The bridge is currently not started. |


# Raspberry PI OS note

When you get the following message on Raspberry PI OS:
```
#
# Fatal error in , line 0
# unreachable code
```

Read this: https://blog.samcater.com/fix-workaround-rpi4-docker-libseccomp2-docker-20/
Conclusion:

```
$ sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 04EE7237B7D453EC 648ACFD622F3D138
$ echo 'deb http://httpredir.debian.org/debian buster-backports main contrib non-free' | sudo tee -a /etc/apt/sources.list.d/debian-backports.list
$ sudo apt update
$ sudo apt install libseccomp2 -t buster-backports
```
