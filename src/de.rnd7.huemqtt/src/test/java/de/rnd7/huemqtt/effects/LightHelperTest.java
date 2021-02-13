package de.rnd7.huemqtt.effects;

import de.rnd7.huemqtt.hue.LightStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LightHelperTest {
    @Test
    void test_turn_off() {
        final LightStub light = new LightStub();
        LightHelper.turnOff(light);

        assertEquals(new ColorXY(), new ColorXY(light.getState().getXy()));
    }
}
