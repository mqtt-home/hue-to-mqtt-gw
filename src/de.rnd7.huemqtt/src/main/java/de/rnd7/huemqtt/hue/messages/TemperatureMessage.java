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

    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public BigDecimal getTemperature() {
        return this.temperature;
    }
}
