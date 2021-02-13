package de.rnd7.huemqtt.hue;

import com.google.common.collect.ImmutableSet;
import de.rnd7.huemqtt.effects.ColorXY;
import de.rnd7.huemqtt.effects.LightEffectData;
import de.rnd7.huemqtt.effects.NotifyAndRestoreLights;
import de.rnd7.huemqtt.effects.NotifyAndTurnOffLights;
import de.rnd7.huemqtt.hue.messages.LightMessage;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.Message;
import de.rnd7.mqttgateway.PublishMessage;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class LightDevice extends HueDevice {
    private final Light light;
    private final String getTopic;
    private final String setTopic;
    private final String setEffectTopic;
    private final ImmutableSet<String> topics;
    private State state;
    private static final Logger logger = LoggerFactory.getLogger(LightDevice.class);

    public LightDevice(final Light light, final String topic, final String id) {
        super(topic, id);
        this.light = light;
        this.state = light.getState();

        this.getTopic = topic + "/get";
        this.setTopic = topic +  "/set";
        this.setEffectTopic = topic +  "/setEffect";

        this.topics = ImmutableSet.of(this.getTopic, this.setTopic, this.setEffectTopic, topic);
    }

    public String getGetTopic() {
        return this.getTopic;
    }

    public Light getLight() {
        return this.light;
    }

    @Override
    public void triggerUpdate() {
        final State next = this.light.getState();
        if (!Objects.equals(this.state, next)) {
            postUpdate(next);
        }
    }

    private void postUpdate(final State next) {
        final LightMessage message = LightMessage.fromState(next);
        this.state = next;

        Events.post(PublishMessage.absolute(this.getTopic(), this.gson.toJson(message)));
    }

    @Override
    public boolean apply(final Message message) {
        if (this.topics.contains(message.getTopic())) {
            return onMessage(message);
        }
        return false;
    }

    @Override
    protected boolean onMessage(final Message message) {
        if (message.getRaw() == null) {
            return false;
        }

        if (message.getTopic().equals(this.getTopic)) {
            postUpdate(getLight().getState());
        }
        else if (message.getTopic().equals(this.setTopic)) {
            setLightState(this.gson.fromJson(message.getRaw(), LightMessage.class));
        }
        else if (message.getTopic().equals(this.setEffectTopic)) {
            applyEffect(this.gson.fromJson(message.getRaw(), LightEffectData.class));
        }
        else {
            return false;
        }
        return true;
    }

    private void setLightState(final LightMessage msg) {
        if (msg.getColor() != null) {
            this.light.setState(State.builder()
                .xy(Arrays.asList(msg.getColor().getX(), msg.getColor().getY()))
                .brightness(msg.getBrightness())
                .on(msg.getState() == LightMessage.LightState.ON));
        }
        else if (msg.getColorTemp() != null) {
            this.light.setState(State.builder()
                .colorTemperatureInMireks(msg.getColorTemp())
                .brightness(msg.getBrightness())
                .on(msg.getState() == LightMessage.LightState.ON));
        }
        else {
            if (msg.getState() == LightMessage.LightState.ON) {
                this.light.turnOn();
            }
            else {
                this.light.turnOff();
            }
        }
    }

    private void applyEffect(final LightEffectData data) {
        switch (data.getEffect()) {
            case notify_restore:
                logger.info("notify_restore {}", this.light.getName());
                new NotifyAndRestoreLights(this.light, data.getColors().toArray(new ColorXY[0]))
                    .notifiy(data.getDuration());
                return;
            case notify_off:
                logger.info("notify_off {}", this.light.getName());
                new NotifyAndTurnOffLights(this.light, data.getColors().toArray(new ColorXY[0]))
                    .notifiy(data.getDuration());
                return;
            default:
                logger.error("Unknown effect {}", data.getEffect());
        }
    }
}
