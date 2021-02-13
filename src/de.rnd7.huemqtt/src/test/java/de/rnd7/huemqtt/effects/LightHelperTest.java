package de.rnd7.huemqtt.effects;

import de.rnd7.huemqtt.hue.LightStub;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LightHelperTest {
    @Test
    void test_turn_off() {
        final LightStub light = new LightStub();
        LightHelper.turnOff(light);

        assertEquals(new ColorXY(), new ColorXY(light.getState().getXy()));
    }

    @Test
    void test_with_state() {
        final LightStub light = new LightStub();
        light.turnOff();
        light.clearHistroy();

        LightHelper.withState(light::turnOn, light);

        assertFalse(light.isOn());
        light.assertStates(true, false, false);
    }

    @Test
    void test_with_off() {
        final LightStub light = new LightStub();
        light.turnOn();
        light.clearHistroy();

        LightHelper.withOff(light::turnOn, light);

        assertFalse(light.isOn());

        light.assertStates(true, false, false);
    }

    @Test
    void test_process_no_task() {
    	LightHelper.processTasksWithPostDelay(Collections.emptyList(), Duration.ZERO);
    	assertTrue(true, "processing no tasks should be successful");
    }

    long current;

    @Test
    void test_process_tasks() {
        this.current = System.currentTimeMillis();
        final List<Runnable> tasks = Arrays.asList(this::assetNoDelay,
            this::assetDidDelay,
            this::assetDidDelay);

        LightHelper.processTasksWithPostDelay(tasks, Duration.ofMillis(500));

        assetDidDelay();
    }
    private void assetNoDelay() {
        this.current = System.currentTimeMillis() - this.current;
        assertTrue(this.current < 400);
    }

    private void assetDidDelay() {
        this.current = System.currentTimeMillis() - this.current;
        assertTrue(this.current > 500);
    }
}
