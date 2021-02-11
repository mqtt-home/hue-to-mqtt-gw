package de.rnd7.huemqtt.hue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DurationAdapterTest {
    public static class TestEntity {
        private Duration duration;

        public TestEntity setDuration(final Duration duration) {
            this.duration = duration;
            return this;
        }
    }

    @Test
    void test_read() {
        final Gson gson = HueDevice.createParser();
        final TestEntity entity = gson.fromJson("{\"duration\":1500}", TestEntity.class);
        assertEquals(1500L, entity.duration.toMillis());
    }

    @Test
    void test_write() {
        final TestEntity testEntity = new TestEntity().setDuration(Duration.ofMillis(4321L));
        final Gson gson = HueDevice.createParser();
        final String entity = gson.toJson(testEntity);
        assertEquals("{\"duration\":4321}", entity);
    }
}
