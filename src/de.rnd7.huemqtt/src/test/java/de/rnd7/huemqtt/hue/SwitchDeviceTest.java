package de.rnd7.huemqtt.hue;

import io.github.zeroone3010.yahueapi.Switch;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SwitchDeviceTest {
    @Test
    void test_null_event() {
        final Switch sw = Mockito.mock(Switch.class);
        Mockito.when(sw.getLastUpdated()).then((i) -> ZonedDateTime.now());
        Mockito.when(sw.getId()).thenReturn("switch");

    	final SwitchDevice device = new SwitchDevice(sw,"","");
        device.triggerUpdate();

        assertTrue(true, "Sending a null message must not throw an exception.");
    }
}
