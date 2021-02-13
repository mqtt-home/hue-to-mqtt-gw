package de.rnd7.huemqtt.hue.messages;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

public class DaylightMessage {

    public static DaylightMessage fromState(final boolean daylight, final ZonedDateTime lastUpdated) {
        final DaylightMessage message = new DaylightMessage();

        message.daylight = daylight;
        message.lastUpdated = lastUpdated;

        return message;
    }

    @SerializedName("daylight")
    private boolean daylight;

    @SerializedName("last-updated")
    private ZonedDateTime lastUpdated;

    public DaylightMessage setDaylight(final boolean daylight) {
        this.daylight = daylight;
        return this;
    }

    public boolean isDaylight() {
        return this.daylight;
    }

    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public DaylightMessage setLastUpdated(final ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }
}
