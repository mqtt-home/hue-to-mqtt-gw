package de.rnd7.huemqtt.hue.messages;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

public class AmbientMessage {

    public static AmbientMessage fromState(final boolean dark, final boolean daylight, final int lightLevel, final ZonedDateTime lastUpdated) {
        final AmbientMessage message = new AmbientMessage();

        message.dark = dark;
        message.daylight = daylight;
        message.lightLevel = lightLevel;
        message.lastUpdated = lastUpdated;

        return message;
    }

    private boolean dark;
    private boolean daylight;

    @SerializedName("last-level")
    private int lightLevel;

    @SerializedName("last-updated")
    private ZonedDateTime lastUpdated;

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public boolean isDark() {
        return dark;
    }

    public boolean isDaylight() {
        return daylight;
    }

    public int getLightLevel() {
        return lightLevel;
    }
}
