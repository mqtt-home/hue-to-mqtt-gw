package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.LogExtension;
import io.github.zeroone3010.yahueapi.Switch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import java.time.ZonedDateTime;

class SwitchDeviceTest {
    @RegisterExtension
    public final LogExtension logs = new LogExtension(SwitchDevice.class);

    @Test
    void test_null_event() {
        final Switch sw = Mockito.mock(Switch.class);
        Mockito.when(sw.getLastUpdated()).then((i) -> ZonedDateTime.now());
        Mockito.when(sw.getId()).thenReturn("switch");

    	final SwitchDevice device = new SwitchDevice(sw,"","");
        device.triggerUpdate();

        this.logs.assertMessages(
            "[INFO] Switch event for switch switch is null - do not publish"
        );
    }
}
