package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.Events;
import de.rnd7.huemqtt.mqtt.LightMessage;
import de.rnd7.huemqtt.mqtt.Message;
import de.rnd7.huemqtt.mqtt.PublishMessage;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.State;

import java.util.Arrays;
import java.util.Objects;

public class LightDevice extends HueDevice {
    private final Light light;
    private State state;
    private final String setLightTopic;

    public LightDevice(final Light light, final String topic, final String id) {
        super(topic, id);
        this.light = light;
        this.state = light.getState();

        this.setLightTopic = getTopic() + "/set";
    }

    public Light getLight() {
        return light;
    }

    @Override
    public void triggerUpdate() {
        final State next = light.getState();
        if (!Objects.equals(state, next)) {
            final LightMessage message = LightMessage.fromState(next);
            this.state = next;

            Events.post(new PublishMessage(this.getTopic(), gson.toJson(message)));
        }
    }

    public LightMessage getMessage() {
        return LightMessage.fromState(light.getState());
    }

    @Override
    public boolean apply(final Message message) {
        if (message.getTopic().equals(this.setLightTopic)) {
            return onMessage(message);
        }
        else {
            return false;
        }
    }

    @Override
    protected boolean onMessage(final Message message) {
        final LightMessage msg = gson.fromJson(message.getRaw(), LightMessage.class);

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

        return true;
    }
}
