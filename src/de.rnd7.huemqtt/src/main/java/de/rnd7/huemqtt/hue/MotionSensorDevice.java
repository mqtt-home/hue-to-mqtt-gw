package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.Events;
import de.rnd7.huemqtt.mqtt.Message;
import de.rnd7.huemqtt.mqtt.MotionMessage;
import de.rnd7.huemqtt.mqtt.PublishMessage;
import io.github.zeroone3010.yahueapi.MotionSensor;

import java.time.ZonedDateTime;
import java.util.Objects;

public class MotionSensorDevice extends HueDevice {
    private final MotionSensor device;
    private ZonedDateTime lastUpdated;

    public MotionSensorDevice(final MotionSensor device, final String topic, final String id) {
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

            Events.post(new PublishMessage(this.getTopic(), gson.toJson(message)));
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
