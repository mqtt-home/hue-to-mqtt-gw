package de.rnd7.huemqtt.hue.messages;

import com.google.gson.annotations.SerializedName;
import io.github.zeroone3010.yahueapi.SwitchEvent;

import java.time.ZonedDateTime;

public class SwitchMessage {

    public static SwitchMessage fromState(final SwitchEvent event, final ZonedDateTime lastUpdated) {
        final SwitchMessage message = new SwitchMessage();

        message.button = event.getButton().getNumber();
        message.code = event.getAction().getEventCode();
        message.lastUpdated = lastUpdated;

        return message;
    }

    @SerializedName("button")
    private int button;

    @SerializedName("code")
    private int code;

    @SerializedName("last-updated")
    private ZonedDateTime lastUpdated;

    public SwitchMessage setButton(final int button) {
        this.button = button;
        return this;
    }

    public int getButton() {
        return this.button;
    }

    public SwitchMessage setCode(final int code) {
        this.code = code;
        return this;
    }

    public int getCode() {
        return this.code;
    }

    public SwitchMessage setLastUpdated(final ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }
}
