package de.rnd7.huemqtt.hue.messages;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TemperatureMessage {

    public static TemperatureMessage fromState(final BigDecimal temperature, final ZonedDateTime lastUpdated) {
        final TemperatureMessage message = new TemperatureMessage();

        message.temperature = temperature;
        message.lastUpdated = lastUpdated;

        return message;
    }

    @SerializedName("temperature")
    private BigDecimal temperature;

    @SerializedName("last-updated")
    private ZonedDateTime lastUpdated;

    public TemperatureMessage setTemperature(final BigDecimal temperature) {
        this.temperature = temperature;
        return this;
    }

    public BigDecimal getTemperature() {
        return this.temperature;
    }

    public TemperatureMessage setLastUpdated(final ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

}
