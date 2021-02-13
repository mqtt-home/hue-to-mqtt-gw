package de.rnd7.huemqtt.hue.messages;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SwitchMessageTest {
    @Test
    void test_get_set() {
        final ZonedDateTime now = ZonedDateTime.now();

        final SwitchMessage message = new SwitchMessage()
            .setLastUpdated(now);

        assertEquals(12, message.setButton(12).getButton());
        assertEquals(13, message.setButton(13).getButton());

        assertEquals(14, message.setCode(14).getCode());
        assertEquals(15, message.setCode(15).getCode());

        assertEquals(now, message.getLastUpdated());
    }
}
