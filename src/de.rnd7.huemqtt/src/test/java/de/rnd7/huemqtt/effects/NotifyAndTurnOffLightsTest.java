package de.rnd7.huemqtt.effects;

import de.rnd7.huemqtt.hue.LightStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;

class NotifyAndTurnOffLightsTest {
    private LightStub light;

    @BeforeEach
    public void setup(){
        this.light = new LightStub();
        this.light.turnOn();
        this.light.clearHistroy();
    }

    @Test
    void test_notify_restore() {
        final ColorXY initialColor = new ColorXY(this.light.getState().getXy());

        new NotifyAndTurnOffLights(this.light, ColorConstants.RED)
            .notifiy(Duration.ofMillis(5));

        assertFalse(this.light.isOn());

        this.light.assertStates(true, false, false);
        this.light.assertColors(ColorConstants.RED, initialColor, ColorConstants.WHITE);
    }

    @Test
    void test_notify_restore_multi_color() {
        final ColorXY initialColor = new ColorXY(this.light.getState().getXy());

        new NotifyAndTurnOffLights(this.light, ColorConstants.RED, ColorConstants.GREEN)
            .notifiy(Duration.ofMillis(5));

        assertFalse(this.light.isOn());

        this.light.assertStates(true, true, false, false);
        this.light.assertColors(ColorConstants.RED, ColorConstants.GREEN, initialColor, ColorConstants.WHITE);
    }


}
