package de.rnd7.huemqtt.effects;

import de.rnd7.huemqtt.hue.LightStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LightHelperTest {
    @Test
    void test_turn_off() {
        final LightStub light = new LightStub();
        LightHelper.turnOff(light);

        assertEquals(new ColorXY(), new ColorXY(light.getState().getXy()));
    }

    @Test
    void test_with_state() {
        final LightStub light = new LightStub();
        light.turnOff();
        light.clearHistroy();

        LightHelper.withState(light::turnOn, light);

        assertFalse(light.isOn());
        light.assertStates(true, false, false);
    }

    @Test
    void test_with_off() {
        final LightStub light = new LightStub();
        light.turnOn();
        light.clearHistroy();

        LightHelper.withOff(light::turnOn, light);

        assertFalse(light.isOn());

        light.assertStates(true, false, false);
    }
}
