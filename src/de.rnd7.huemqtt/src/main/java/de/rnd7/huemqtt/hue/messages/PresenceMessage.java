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

    @SerializedName("presence")
    private boolean presence;

    @SerializedName("last-updated")
    private ZonedDateTime lastUpdated;

    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public boolean isPresence() {
        return this.presence;
    }
}
