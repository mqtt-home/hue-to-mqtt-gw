package de.rnd7.huemqtt.hue.messages;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

public class PresenceMessage {

    public static PresenceMessage fromState(final boolean presence, final ZonedDateTime lastUpdated) {
        final PresenceMessage message = new PresenceMessage();

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
