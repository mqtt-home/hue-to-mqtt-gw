package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.hue.messages.DaylightMessage;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.PublishMessage;
import io.github.zeroone3010.yahueapi.DaylightSensor;

import java.time.ZonedDateTime;
import java.util.Objects;

public class DaylightSensorDevice extends HueDevice {
    private final DaylightSensor device;
    private ZonedDateTime lastUpdated;
    private DaylightMessage message;

    public DaylightSensorDevice(final DaylightSensor device, final String topic, final String id) {
        super(topic, id);
        this.device = device;
        this.lastUpdated = device.getLastUpdated();
    }

    @Override
    public void triggerUpdate() {
        final ZonedDateTime lastUpdated = this.device.getLastUpdated();
        if (!Objects.equals(this.lastUpdated, lastUpdated)) {
            this.message = DaylightMessage.fromState(this.device.isDaylightTime(), lastUpdated);
            this.lastUpdated = lastUpdated;

            Events.post(PublishMessage.absolute(this.getTopic(), this.gson.toJson(this.message)));
        }
    }

    public DaylightMessage getMessage() {
        return this.message;
    }

}
