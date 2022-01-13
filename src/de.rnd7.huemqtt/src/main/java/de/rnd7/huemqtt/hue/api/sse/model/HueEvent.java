package de.rnd7.huemqtt.hue.api.sse.model;

import java.util.ArrayList;
import java.util.List;

public class HueEvent {
    private String id;
    private HueEventType type;
    private List<HueEventData> data = new ArrayList<>();

    public String getId() {
        return id;
    }

    public HueEvent setId(final String id) {
        this.id = id;
        return this;
    }

    public HueEventType getType() {
        return type;
    }

    public List<HueEventData> getData() {
        return data;
    }

    public HueEvent setType(final HueEventType type) {
        this.type = type;
        return this;
    }
}
