package de.rnd7.huemqtt.hue;

import de.rnd7.mqttgateway.Message;
import io.github.zeroone3010.yahueapi.Light;
import org.junit.jupiter.api.Test;

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
}
