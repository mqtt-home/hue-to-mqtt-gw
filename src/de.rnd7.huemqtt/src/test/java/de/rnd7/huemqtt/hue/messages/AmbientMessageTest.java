package de.rnd7.huemqtt.hue.messages;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmbientMessageTest {
    @Test
    void test_get_set() {
        final ZonedDateTime now = ZonedDateTime.now();

        final AmbientMessage message = new AmbientMessage()
            .setLastUpdated(now);

        assertFalse(message.setDark(false).isDark());
        assertTrue(message.setDark(true).isDark());

        assertFalse(message.setDaylight(false).isDaylight());
        assertTrue(message.setDaylight(true).isDaylight());

        assertEquals(55, message.setLightLevel(55).getLightLevel());
        assertEquals(56, message.setLightLevel(56).getLightLevel());

        assertEquals(now, message.getLastUpdated());
    }
}
