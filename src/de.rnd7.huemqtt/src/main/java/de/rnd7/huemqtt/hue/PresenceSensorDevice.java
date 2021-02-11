package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.hue.messages.PresenceMessage;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.PublishMessage;
import io.github.zeroone3010.yahueapi.PresenceSensor;

import java.time.ZonedDateTime;
import java.util.Objects;

public class PresenceSensorDevice extends HueDevice {
    private final PresenceSensor device;
    private ZonedDateTime lastUpdated;
    private PresenceMessage message;

    public PresenceSensorDevice(final PresenceSensor device, final String topic, final String id) {
        super(topic, id);
        this.device = device;
        this.lastUpdated = device.getLastUpdated();
    }

    public PresenceMessage getMessage() {
        return this.message;
    }

    @Override
    public void triggerUpdate() {
        final ZonedDateTime lastUpdated = this.device.getLastUpdated();
        if (!Objects.equals(this.lastUpdated, lastUpdated)) {
            this.message = PresenceMessage.fromState(this.device.isPresence(), lastUpdated);
            this.lastUpdated = lastUpdated;

            Events.post(PublishMessage.absolute(this.getTopic(), this.gson.toJson(this.message)));
        }
    }

}
