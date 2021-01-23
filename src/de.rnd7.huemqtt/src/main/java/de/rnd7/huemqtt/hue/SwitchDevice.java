package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.hue.messages.SwitchMessage;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.Message;
import de.rnd7.mqttgateway.PublishMessage;
import io.github.zeroone3010.yahueapi.Switch;
import io.github.zeroone3010.yahueapi.SwitchEvent;

import java.time.ZonedDateTime;
import java.util.Objects;

public class SwitchDevice extends HueDevice {
    private final Switch device;
    private ZonedDateTime lastUpdated;

    public SwitchDevice(final Switch device, final String topic, final String id) {
        super(topic, id);
        this.device = device;
        this.lastUpdated = device.getLastUpdated();
    }

    public Switch getDevice() {
        return device;
    }

    @Override
    public void triggerUpdate() {
        final ZonedDateTime lastUpdated = device.getLastUpdated();
        if (!Objects.equals(this.lastUpdated, lastUpdated)) {
            final SwitchEvent next = device.getLatestEvent();
            final SwitchMessage message = SwitchMessage.fromState(next, lastUpdated);
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
