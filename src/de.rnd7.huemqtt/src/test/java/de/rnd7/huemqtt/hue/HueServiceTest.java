package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.hue.api.HueAbstraction;
import features.HueAbstractionStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class HueServiceTest {

    @AfterEach
    public void tearDown(){
        HueService.shutdown();
    }

    @Test
    void test_start_twice() {
        final HueAbstraction hue = new HueAbstractionStub();
        HueService.start(hue, "hue");

    	assertThrows(IllegalStateException.class, () ->
            HueService.start(hue, "hue")
        );
    }
}
