package de.rnd7.huemqtt.hue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;

public abstract class HueDevice extends Device {
    protected final Gson gson = new GsonBuilder()
        .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
        .registerTypeAdapter(BigDecimalTypeAdapter.class, new BigDecimalTypeAdapter())
        .create();

    public HueDevice(final String topic, final String id) {
        super(topic, id);
    }

    public abstract void triggerUpdate();
}
