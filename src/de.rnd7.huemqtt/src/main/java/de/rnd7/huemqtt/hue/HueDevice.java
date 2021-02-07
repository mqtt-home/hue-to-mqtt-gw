package de.rnd7.huemqtt.hue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.rnd7.mqttgateway.Message;

import java.time.Duration;
import java.time.ZonedDateTime;

public abstract class HueDevice extends Device {
    public static Gson createParser() {
        return new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
            .registerTypeAdapter(BigDecimalTypeAdapter.class, new BigDecimalTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    }

    protected final Gson gson = createParser();

    public HueDevice(final String topic, final String id) {
        super(topic, id);
    }

    public abstract void triggerUpdate();

    @Override
    public boolean apply(final Message message) {
        return false;
    }

    @Override
    protected boolean onMessage(final Message message) {
        return false;
    }
}
