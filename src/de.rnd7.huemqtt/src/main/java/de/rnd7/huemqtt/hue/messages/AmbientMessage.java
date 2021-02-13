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

    public boolean isDark() {
        return this.dark;
    }

    public AmbientMessage setDark(final boolean dark) {
        this.dark = dark;
        return this;
    }

    public boolean isDaylight() {
        return this.daylight;
    }

    public AmbientMessage setDaylight(final boolean daylight) {
        this.daylight = daylight;
        return this;
    }

    public int getLightLevel() {
        return this.lightLevel;
    }

    public AmbientMessage setLightLevel(final int lightLevel) {
        this.lightLevel = lightLevel;
        return this;
    }

    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public AmbientMessage setLastUpdated(final ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }
}
