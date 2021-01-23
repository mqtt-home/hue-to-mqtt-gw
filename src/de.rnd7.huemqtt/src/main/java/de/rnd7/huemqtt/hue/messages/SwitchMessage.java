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

    private int button;
    private int code;

    @SerializedName("last-updated")
    private ZonedDateTime lastUpdated;

    public int getButton() {
        return button;
    }

    public int getCode() {
        return code;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }
}
