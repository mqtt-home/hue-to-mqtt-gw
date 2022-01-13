package de.rnd7.huemqtt.hue.api.sse.model;

public class HueEventData {
    private String id;
    private HueEventDataType type;

    public String getId() {
        return id;
    }

    public HueEventDataType getType() {
        return type;
    }
}
