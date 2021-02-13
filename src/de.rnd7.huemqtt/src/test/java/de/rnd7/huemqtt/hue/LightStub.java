package de.rnd7.huemqtt.hue;

import io.github.zeroone3010.yahueapi.Color;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.LightType;
import io.github.zeroone3010.yahueapi.State;

public class LightStub implements Light {
    private static final Color WHITE = Color.of(1f,1f,1f);
    private State state = State.builder().color(WHITE).off();
    private int brightness = 254;

    @Override
    public String getName() {
        return "Stubbed Light";
    }

    @Override
    public void turnOn() {
        this.state = State.builder().color(WHITE).on();
    }

    @Override
    public void turnOff() {
        this.state = State.builder().color(WHITE).off();
    }

    @Override
    public boolean isOn() {
        return this.state.getOn();
    }

    @Override
    public boolean isReachable() {
        return true;
    }

    @Override
    public void setBrightness(final int brightness) {
        this.brightness = brightness;
    }

    @Override
    public LightType getType() {
        return LightType.COLOR;
    }

    @Override
    public void setState(final State state) {
        this.state = state;
    }

    @Override
    public State getState() {
        return this.state;
    }
}
