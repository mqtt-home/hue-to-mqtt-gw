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

    @SerializedName("dark")
    private boolean dark;

    @SerializedName("daylight")
    private boolean daylight;

    @SerializedName("last-level")
    private int lightLevel;

    @SerializedName("last-updated")
    private ZonedDateTime lastUpdated;

    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public boolean isDark() {
        return this.dark;
    }

    public boolean isDaylight() {
        return this.daylight;
    }

    public int getLightLevel() {
        return this.lightLevel;
    }
}
