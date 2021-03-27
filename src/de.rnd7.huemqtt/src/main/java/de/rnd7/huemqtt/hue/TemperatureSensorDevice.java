package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.hue.messages.TemperatureMessage;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.PublishMessage;
import io.github.zeroone3010.yahueapi.TemperatureSensor;

import java.time.ZonedDateTime;
import java.util.Objects;

public class TemperatureSensorDevice extends HueDevice {
    private final TemperatureSensor device;
    private ZonedDateTime lastUpdated;
    private TemperatureMessage message;

    public TemperatureSensorDevice(final TemperatureSensor device, final String topic, final String id) {
        super(topic, id);
        this.device = device;
        this.lastUpdated = device.getLastUpdated();
    }

    public TemperatureMessage getMessage() {
        return this.message;
    }

    @Override
    public void triggerUpdate() {
        final var lastUpdated = this.device.getLastUpdated();
        if (!Objects.equals(this.lastUpdated, lastUpdated)) {
            this.message = TemperatureMessage.fromState(this.device.getDegreesCelsius(), lastUpdated);
            this.lastUpdated = lastUpdated;

            Events.post(PublishMessage.absolute(this.getTopic(), this.gson.toJson(this.message)));
        }
    }

}
