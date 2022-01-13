package de.rnd7.huemqtt.hue.api.sse.model;

import com.google.gson.annotations.SerializedName;

public enum HueEventDataType {
    @SerializedName("motion")
    MOTION,
/*
[
  {
    "data": [
      {
        "owner": {
          "rtype": "device",
          "rid": "f3eeb7b5-8d4f-4acc-a59f-6297edaa1a25"
        },
        "motion": {
          "motion": false,
          "motion_valid": true
        },
        "id_v1": "/sensors/94",
        "id": "5db04665-1ac8-4d5a-8e93-5ff2c2f9cb31",
        "type": "motion"
      }
    ],
    "creationtime": "2022-01-12T16:35:52Z",
    "id": "e19df392-1d27-47e9-a516-6cbbec5e935a",
    "type": "update"
  }
]
*/

    @SerializedName("button")
    BUTTON,
/*
    [
  {
    "creationtime": "2022-01-12T18:05:26Z",
    "data": [
      {
        "button": {
          "last_event": "initial_press"
        },
        "id": "91181235-3297-4503-a238-e9bcd5657bee",
        "id_v1": "/sensors/86",
        "owner": {
          "rid": "27cd9a40-a398-4c5b-b11f-87c8fdf3c4fc",
          "rtype": "device"
        },
        "type": "button"
      }
    ],
    "id": "1ceaf7e6-541e-43dd-802e-d7b17c15a3f7",
    "type": "update"
  }
]

[{"creationtime":"2022-01-12T18:08:12Z","data":[{"button":{"last_event":"initial_press"},"id":"cbfb0e8d-31ad-48f2-a7f8-51cf6f0d7f00","id_v1":"/sensors/118","owner":{"rid":"0109145c-93e6-40e1-87cc-f23895b7ca28","rtype":"device"},"type":"button"}],"id":"b6cfd39f-db96-4ba8-8219-e2c72516ec42","type":"update"}]
  */

    @SerializedName("light")
    LIGHT,

    @SerializedName("light_level")
    LIGHT_LEVEL,

    @SerializedName("grouped_light")
    GROUPED_LIGHT,


/*
[
  {
    "creationtime": "2022-01-12T18:06:17Z",
    "data": [
      {
        "id": "543ec0eb-825e-4864-a972-79bccffba4f6",
        "id_v1": "/sensors/108",
        "light": {
          "light_level": 0,
          "light_level_valid": true
        },
        "owner": {
          "rid": "06950c5b-b07c-492e-99e7-24ba3638c706",
          "rtype": "device"
        },
        "type": "light_level"
      }
    ],
    "id": "337711ef-edc4-4013-ab18-dda29096c476",
    "type": "update"
  }
]


 */

    @SerializedName("temperature")
    TEMPERATURE,
/*
    [
  {
    "creationtime": "2022-01-12T18:04:25Z",
    "data": [
      {
        "id": "161eab6e-4a2d-472f-9584-0c7663c9e097",
        "id_v1": "/sensors/96",
        "owner": {
          "rid": "f3eeb7b5-8d4f-4acc-a59f-6297edaa1a25",
          "rtype": "device"
        },
        "temperature": {
          "temperature": 22.860000610351562,
          "temperature_valid": true
        },
        "type": "temperature"
      }
    ],
    "id": "a0807b0c-87d4-4ce6-9263-f5f8476b7717",
    "type": "update"
  }
]
  */
}
