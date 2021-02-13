package de.rnd7.huemqtt.hue.messages;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemperatureMessageTest {
    @Test
    void test_get_set() {
        final ZonedDateTime now = ZonedDateTime.now();

        final TemperatureMessage message = new TemperatureMessage()
            .setTemperature(new BigDecimal("22.3"))
            .setLastUpdated(now);

        assertEquals(new BigDecimal("22.3"), message
            .setTemperature(new BigDecimal("22.3")).getTemperature());
        assertEquals(new BigDecimal("18.9"), message
            .setTemperature(new BigDecimal("18.9")).getTemperature());

        assertEquals(now, message.getLastUpdated());
    }
}
