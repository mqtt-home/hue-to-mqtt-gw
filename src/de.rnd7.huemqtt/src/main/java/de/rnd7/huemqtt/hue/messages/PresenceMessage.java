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

    @SerializedName("presence_code")
    private int presenceCode;

    @SerializedName("last-updated")
    private ZonedDateTime lastUpdated;

    public PresenceMessage setPresence(final boolean presence) {
        this.presence = presence;
        this.presenceCode = presence ? 1 : 0;
        return this;
    }

    public boolean isPresence() {
        return this.presence;
    }

    public int getPresenceCode() {
        return presenceCode;
    }

    public PresenceMessage setLastUpdated(final ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }
}
