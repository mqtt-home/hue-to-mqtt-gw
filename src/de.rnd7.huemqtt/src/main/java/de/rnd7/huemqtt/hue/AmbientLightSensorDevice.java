package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.hue.messages.AmbientMessage;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.Message;
import de.rnd7.mqttgateway.PublishMessage;
import io.github.zeroone3010.yahueapi.AmbientLightSensor;

import java.time.ZonedDateTime;
import java.util.Objects;

public class AmbientLightSensorDevice extends HueDevice {
    private final AmbientLightSensor device;
    private ZonedDateTime lastUpdated;
    private AmbientMessage message;

    public AmbientLightSensorDevice(final AmbientLightSensor device, final String topic, final String id) {
        super(topic, id);
        this.device = device;
        this.lastUpdated = device.getLastUpdated();
    }

    public AmbientMessage getMessage() {
        return this.message;
    }

    @Override
    public void triggerUpdate() {
        final ZonedDateTime lastUpdated = this.device.getLastUpdated();
        if (!Objects.equals(this.lastUpdated, lastUpdated)) {
            this.message = AmbientMessage.fromState(this.device.isDark(),
                this.device.isDaylight(), this.device.getLightLevel(), lastUpdated);
            this.lastUpdated = lastUpdated;

            Events.post(PublishMessage.absolute(this.getTopic(), this.gson.toJson(this.message)));
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
