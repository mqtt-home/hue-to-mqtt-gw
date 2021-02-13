package de.rnd7.huemqtt.hue.messages;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PresenceMessageTest {
    @Test
    void test_get_set() {
        final ZonedDateTime now = ZonedDateTime.now();

        final PresenceMessage message = new PresenceMessage()
            .setLastUpdated(now);

        assertFalse(message.setPresence(false).isPresence());
        assertTrue(message.setPresence(true).isPresence());

        assertEquals(now, message.getLastUpdated());
    }
}
