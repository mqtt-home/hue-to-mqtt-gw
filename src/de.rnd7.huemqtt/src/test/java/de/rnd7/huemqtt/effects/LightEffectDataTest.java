package de.rnd7.huemqtt.effects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.rnd7.huemqtt.hue.BigDecimalTypeAdapter;
import de.rnd7.huemqtt.hue.DurationAdapter;
import de.rnd7.huemqtt.hue.HueDevice;
import de.rnd7.huemqtt.hue.ZonedDateTimeTypeAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LightEffectDataTest {

    @ParameterizedTest
    @MethodSource("parseExamples")
    void test_parse(final MessageData data) {
        final LightEffectData expectedData = (LightEffectData) data.getExpectedData();
        final LightEffectData effectData = HueDevice.createParser().fromJson(data.getMessage(), LightEffectData.class);
        assertEquals(expectedData.getEffect(), effectData.getEffect());
        assertEquals(expectedData.getDuration(), effectData.getDuration());
        assertArrayEquals(expectedData.getColors().toArray(new ColorXY[0]),
            effectData.getColors().toArray(new ColorXY[0]));
    }

    private static Stream<MessageData> parseExamples() {
        return Stream.of(
            new MessageData(
                "{\"effect\": \"notify_restore\", \"colors\": [{\"x\": 0.6758, \"y\": 0.2953}], \"duration\": 1000}",
                new LightEffectData()
                    .setEffect(LightEffect.notify_restore)
                    .addColor(new ColorXY(0.6758f, 0.2953f))
                    .setDuration(Duration.ofMillis(1000))
            ),
            new MessageData(
                "{\"effect\": \"notify_restore\", \"colors\": [{\"x\": 0.6758, \"y\": 0.2953}], \"duration\": 500}",
                new LightEffectData()
                    .setEffect(LightEffect.notify_restore)
                    .addColor(new ColorXY(0.6758f, 0.2953f))
                    .setDuration(Duration.ofMillis(500))
            ),
            new MessageData(
                "{\"effect\": \"notify_restore\", \"colors\": [{\"x\": 0.3687, \"y\": 0.371}]}",
                new LightEffectData()
                    .setEffect(LightEffect.notify_restore)
                    .addColor(new ColorXY(0.3687f, 0.371f))
                .setDuration(Duration.ofSeconds(1))
            )
        );
    }
}
