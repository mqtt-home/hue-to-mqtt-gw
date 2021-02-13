package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.effects.ColorConstants;
import de.rnd7.huemqtt.effects.ColorXY;
import de.rnd7.huemqtt.effects.LightEffect;
import de.rnd7.huemqtt.effects.LightEffectData;
import de.rnd7.mqttgateway.Message;
import io.github.zeroone3010.yahueapi.Light;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static de.rnd7.huemqtt.hue.HueDevice.createParser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LightDeviceTest {

    @Test
    void test_null_message() {
        final Light light = new LightStub();
        light.turnOn();

        new LightDevice(light,"", "")
            .onMessage(new Message("/set", null));

        assertTrue(light.isOn());
    }

    @Test
    void test_color_temperature_message() {
        final Light light = new LightStub();
        light.turnOn();

        new LightDevice(light,"", "")
            .onMessage(new Message("/set", "{\"state\":\"OFF\",\"brightness\":1,\"color_temp\":366}"));

        assertFalse(light.isOn());
        assertEquals(1, light.getState().getBri());
        assertEquals(366, light.getState().getCt());
    }
    
    @Test
    void test_color_message() {
        final Light light = new LightStub();
        light.turnOff();

        new LightDevice(light,"", "")
            .onMessage(new Message("/set", "{\n" +
                "    \"state\":\"ON\",\n" +
                "    \"brightness\":254,\n" +
                "    \"color\":{\"x\":0.3691,\"y\":0.3719}\n" +
                "}"));

        assertTrue(light.isOn());
        assertEquals(254, light.getState().getBri());
        assertEquals(0.3691f, light.getState().getXy().get(0), 0.001);
        assertEquals(0.3719f, light.getState().getXy().get(1), 0.001);
    }

    @Test
    void test_turn_on() {
        final Light light = new LightStub();
        light.turnOff();

        new LightDevice(light,"", "")
            .onMessage(new Message("/set", "{\n" +
                "    \"state\":\"ON\"\n" +
                "}"));

        assertTrue(light.isOn());
    }

    @Test
    void test_turn_off() {
        final Light light = new LightStub();
        light.turnOn();

        new LightDevice(light,"", "")
            .onMessage(new Message("/set", "{\n" +
                "    \"state\":\"OFF\"\n" +
                "}"));

        assertFalse(light.isOn());
    }

    @Test
    void test_notify_restore_effect() {
        final LightStub light = new LightStub();
        light.turnOn();
        light.clearHistroy();
        final ColorXY initialColor = new ColorXY(light.getState().getXy());

        final String message = createParser().toJson(new LightEffectData()
            .setEffect(LightEffect.notify_restore)
            .setDuration(Duration.ofMillis(5))
            .addColor(ColorConstants.RED)
            .addColor(ColorConstants.GREEN));

        new LightDevice(light,"", "")
            .onMessage(new Message("/setEffect", message));

        light.assertStates(true, true, true);
        light.assertColors(ColorConstants.RED, ColorConstants.GREEN, initialColor);
    }

    @Test
    void test_notify_off_effect() {
        final LightStub light = new LightStub();
        light.turnOn();
        light.clearHistroy();
        final ColorXY initialColor = new ColorXY(light.getState().getXy());

        final String message = createParser().toJson(new LightEffectData()
            .setEffect(LightEffect.notify_off)
            .setDuration(Duration.ofMillis(5))
            .addColor(ColorConstants.RED)
            .addColor(ColorConstants.GREEN));

        new LightDevice(light,"", "")
            .onMessage(new Message("/setEffect", message));

        light.assertStates(true, true, false, false);
        light.assertColors(ColorConstants.RED, ColorConstants.GREEN, initialColor, ColorConstants.WHITE);
    }
}
