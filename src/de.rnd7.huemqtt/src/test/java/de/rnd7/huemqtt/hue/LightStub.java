package de.rnd7.huemqtt.hue;

import io.github.zeroone3010.yahueapi.Color;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.LightType;
import io.github.zeroone3010.yahueapi.State;

import java.util.ArrayList;
import java.util.List;

public class LightStub implements Light {
    private static final Color WHITE = Color.of(1f,1f,1f);
    private State state = State.builder().color(WHITE).off();
    private int brightness = 254;

    private final List<State> history = new ArrayList<>();

    @Override
    public String getName() {
        return "Stubbed Light";
    }

    @Override
    public void turnOn() {
        setState(State.builder().color(WHITE).on());
    }

    @Override
    public void turnOff() {
        setState(State.builder().color(WHITE).off());
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

        this.history.add(state);
    }

    @Override
    public State getState() {
        return this.state;
    }

    public List<State> getHistory() {
        return this.history;
    }

    public void clearHistroy() {
        this.history.clear();
    }
}
