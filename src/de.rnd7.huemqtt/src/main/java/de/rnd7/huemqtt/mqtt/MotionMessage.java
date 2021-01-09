package de.rnd7.huemqtt.mqtt;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

public class MotionMessage {

    public static MotionMessage fromState(final boolean presence, final ZonedDateTime lastUpdated) {
        final MotionMessage message = new MotionMessage();

        message.presence = presence;
        message.lastUpdated = lastUpdated;

        return message;
    }

    private boolean presence;

    @SerializedName("last-updated")
    private ZonedDateTime lastUpdated;

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public boolean isPresence() {
        return presence;
    }
}
