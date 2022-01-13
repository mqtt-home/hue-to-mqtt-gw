package de.rnd7.huemqtt.hue.api.sse.model;

import com.google.gson.annotations.SerializedName;

public enum HueEventType {
    @SerializedName("update")
    UPDATE,
    @SerializedName("add")
    ADD,
    @SerializedName("delete")
    DELETE,
    @SerializedName("error")
    ERROR
}
