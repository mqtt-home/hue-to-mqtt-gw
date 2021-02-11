package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.hue.messages.SwitchMessage;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.PublishMessage;
import io.github.zeroone3010.yahueapi.Switch;
import io.github.zeroone3010.yahueapi.SwitchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Objects;

public class SwitchDevice extends HueDevice {
    private final Switch device;
    private ZonedDateTime lastUpdated;
    private static final Logger logger = LoggerFactory.getLogger(SwitchDevice.class);

    public SwitchDevice(final Switch device, final String topic, final String id) {
        super(topic, id);
        this.device = device;
        this.lastUpdated = device.getLastUpdated();
    }

    @Override
    public void triggerUpdate() {
        final ZonedDateTime nextTimestamp = this.device.getLastUpdated();
        if (!Objects.equals(this.lastUpdated, nextTimestamp)) {
            final SwitchEvent next = this.device.getLatestEvent();
            this.lastUpdated = nextTimestamp;
            publishEvent(nextTimestamp, next);
        }
    }

    private void publishEvent(final ZonedDateTime nextTimestamp, final SwitchEvent next) {
        if (next != null) {
            final SwitchMessage message = SwitchMessage.fromState(next, nextTimestamp);
            Events.post(PublishMessage.absolute(this.getTopic(), this.gson.toJson(message)));
        }
        else {
            logger.info("Switch event for switch {} is null - do not publish", this.device.getId());
        }
    }
}
