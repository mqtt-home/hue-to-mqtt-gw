package de.rnd7.huemqtt.mqtt;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

public class DaylightMessage {

    public static DaylightMessage fromState(final boolean daylight, final ZonedDateTime lastUpdated) {
        final DaylightMessage message = new DaylightMessage();

        message.daylight = daylight;
        message.lastUpdated = lastUpdated;

        return message;
    }

    private boolean daylight;

    @SerializedName("last-updated")
    private ZonedDateTime lastUpdated;

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public boolean isDaylight() {
        return daylight;
    }
}
