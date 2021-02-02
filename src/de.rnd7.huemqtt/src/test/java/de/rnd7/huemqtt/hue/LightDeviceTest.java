package de.rnd7.huemqtt.hue;

import de.rnd7.mqttgateway.Message;
import io.github.zeroone3010.yahueapi.Light;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LightDeviceTest {

    @Test
    void test_null_message() {
        Light light = Mockito.mock(Light.class);
        new LightDevice(light,"", "")
            .onMessage(new Message("/set", null));
    }

    @Test
    void test_message() {
        Light light = Mockito.mock(Light.class);
        new LightDevice(light,"", "")
            .onMessage(new Message("/set", "{\"state\":\"OFF\",\"brightness\":1,\"color_temp\":366}"));
    }
}
