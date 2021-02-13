package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.effects.ColorConstants;
import de.rnd7.huemqtt.effects.ColorXY;
import de.rnd7.huemqtt.effects.LightEffect;
import de.rnd7.huemqtt.effects.LightEffectData;
import de.rnd7.huemqtt.hue.messages.LightMessage;
import de.rnd7.mqttgateway.Message;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static de.rnd7.huemqtt.hue.HueDevice.createParser;
import static de.rnd7.huemqtt.hue.LightStub.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LightDeviceTest {

    private static class TestData {
        private final LightMessage message;
        private final State expected;

        private TestData(final LightMessage message, final State expected) {
            this.message = message;
            this.expected = expected;
        }
    }

    @Test
    void test_string() {
        final LightDevice device = new LightDevice(new LightStub(), "", "");
        assertTrue(device.toString().startsWith("Device{"));
    }

    public static Stream<TestData> testDataSet() {
        return Stream.of(
            new TestData(null, State.builder().color(WHITE).on()),

            new TestData(new LightMessage()
                .setState(LightMessage.LightState.OFF).setBrightness(1).setColorTemp(366),
                State.builder().colorTemperatureInMireks(366).brightness(1).off()),
            new TestData(new LightMessage()
                .setState(LightMessage.LightState.ON).setBrightness(1).setColorTemp(366),
                State.builder().colorTemperatureInMireks(366).brightness(1).on()),

            new TestData(new LightMessage()
                .setState(LightMessage.LightState.ON)
                .setColor(new LightMessage.Color(ColorConstants.WHITE))
                .setBrightness(254),
                State.builder().xy(ColorConstants.WHITE.getXY()).brightness(254).on()),
            new TestData(new LightMessage()
                .setState(LightMessage.LightState.OFF)
                .setColor(new LightMessage.Color(ColorConstants.WHITE))
                .setBrightness(254),
                State.builder().xy(ColorConstants.WHITE.getXY()).brightness(254).off())
        );
    }
    
    @ParameterizedTest
    @MethodSource(value = "testDataSet")
    void test_set(final TestData data) {
        final String raw = data.message == null ? null : createParser().toJson(data.message);

        final Light light = new LightStub();
        light.turnOn();

        new LightDevice(light,"", "")
            .onMessage(new Message("/set", raw));

        assertEquals(data.expected, light.getState());
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
