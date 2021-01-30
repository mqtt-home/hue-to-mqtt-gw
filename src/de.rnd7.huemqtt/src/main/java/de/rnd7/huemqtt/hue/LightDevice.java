package de.rnd7.huemqtt.hue;

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
    private State state;
    private static final Logger logger = LoggerFactory.getLogger(LightDevice.class);

    public LightDevice(final Light light, final String topic, final String id) {
        super(topic, id);
        this.light = light;
        this.state = light.getState();
    }

    public Light getLight() {
        return light;
    }

    @Override
    public void triggerUpdate() {
        final State next = light.getState();
        if (!Objects.equals(state, next)) {
            postUpdate(next);
        }
    }

    private void postUpdate(final State next) {
        final LightMessage message = LightMessage.fromState(next);
        this.state = next;

        Events.post(PublishMessage.absolute(this.getTopic(), gson.toJson(message)));
    }

    public LightMessage getMessage() {
        return LightMessage.fromState(light.getState());
    }

    @Override
    public boolean apply(final Message message) {
        return message.getTopic().startsWith(getTopic());
    }

    @Override
    protected boolean onMessage(final Message message) {
        if (message.getTopic().equals(getTopic() + "/get")) {
            postUpdate(getLight().getState());
        }
        else if (message.getTopic().equals(getTopic() + "/set")) {
            setLightState(gson.fromJson(message.getRaw(), LightMessage.class));
        }
        else if (message.getTopic().equals(getTopic() + "/setEffect")) {
            applyEffect(gson.fromJson(message.getRaw(), LightEffectData.class));
        }
        else {
            return false;
        }
        return true;
    }

    private void setLightState(final LightMessage msg) {
        if (msg.getColor() != null) {
            light.setState(State.builder()
                .xy(Arrays.asList(msg.getColor().getX(), msg.getColor().getY()))
                .brightness(msg.getBrightness())
                .on(msg.getState() == LightMessage.LightState.ON));
        }
        else if (msg.getColorTemp() != null) {
            light.setState(State.builder()
                .colorTemperatureInMireks(msg.getColorTemp())
                .brightness(msg.getBrightness())
                .on(msg.getState() == LightMessage.LightState.ON));
        }
        else {
            if (msg.getState() == LightMessage.LightState.ON) {
                light.turnOn();
            }
            else {
                light.turnOff();
            }
        }
    }

    private void applyEffect(final LightEffectData data) {
        switch (data.getEffect()) {
            case notify_restore:
                new NotifyAndRestoreLights(light, data.getColors().toArray(new ColorXY[0]))
                    .notifiy(data.getDuration());
                return;
            case notify_off:
                new NotifyAndTurnOffLights(light, data.getColors().toArray(new ColorXY[0]))
                    .notifiy(data.getDuration());
                return;
            default:
                logger.error("Unknown effect {}", data.getEffect());
        }
    }
}
