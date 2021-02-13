package de.rnd7.huemqtt.hue;

import de.rnd7.mqttgateway.Message;
import io.github.zeroone3010.yahueapi.Switch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DeviceTest {
    @Test
    void test_on_message() {
        final Switch sw = Mockito.mock(Switch.class);
        final SwitchDevice device = new SwitchDevice(sw, "", "");
        Assertions.assertFalse(device.onMessage(new Message("", "")));
    }
}
