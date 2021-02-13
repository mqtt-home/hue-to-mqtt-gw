package de.rnd7.huemqtt.hue.messages;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DaylightMessageTest {
    @Test
    void test_get_set() {
        final ZonedDateTime now = ZonedDateTime.now();
        final DaylightMessage message = new DaylightMessage()
            .setLastUpdated(now);

        assertFalse(message.setDaylight(false).isDaylight());
        assertTrue(message.setDaylight(true).isDaylight());

        assertEquals(now, message.getLastUpdated());
    }
}
