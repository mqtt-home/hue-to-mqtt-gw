package de.rnd7.huemqtt.hue;

import features.HueAbstractionStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class HueServiceTest {
    @Test
    void test_start_twice() {
    	HueService.start(new HueAbstractionStub(), "hue");
    	assertThrows(IllegalStateException.class, () ->
            HueService.start(new HueAbstractionStub(), "hue")
        );
    }
}
