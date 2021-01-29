package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.hue.messages.MotionMessage;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.Message;
import de.rnd7.mqttgateway.PublishMessage;
import io.github.zeroone3010.yahueapi.PresenceSensor;

import java.time.ZonedDateTime;
import java.util.Objects;

public class PresenceSensorDevice extends HueDevice {
    private final PresenceSensor device;
    private ZonedDateTime lastUpdated;

    public PresenceSensorDevice(final PresenceSensor device, final String topic, final String id) {
        super(topic, id);
        this.device = device;
        this.lastUpdated = device.getLastUpdated();
    }

    @Override
    public void triggerUpdate() {
        final ZonedDateTime lastUpdated = device.getLastUpdated();
        if (!Objects.equals(this.lastUpdated, lastUpdated)) {
            final MotionMessage message = MotionMessage.fromState(device.isPresence(), lastUpdated);
            this.lastUpdated = lastUpdated;

            Events.post(PublishMessage.absolute(this.getTopic(), gson.toJson(message)));
        }
    }

    @Override
    public boolean apply(final Message message) {
        return false;
    }

    @Override
    protected boolean onMessage(final Message message) {
        return false;
    }
}
