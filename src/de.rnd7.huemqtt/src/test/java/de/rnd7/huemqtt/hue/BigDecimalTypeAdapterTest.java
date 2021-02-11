package de.rnd7.huemqtt.hue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BigDecimalTypeAdapterTest {
    public static class TestEntity {
        private BigDecimal value;

        public TestEntity setValue(final BigDecimal value) {
            this.value = value;
            return this;
        }
    }

    @Test
    void test_read() {
        final Gson gson = HueDevice.createParser();
        final TestEntity entity = gson.fromJson("{\"value\":1.5}", TestEntity.class);
        assertEquals(new BigDecimal("1.5"), entity.value);
    }

    @Test
    void test_write() {
        final TestEntity testEntity = new TestEntity().setValue(new BigDecimal("1.2"));
        final Gson gson = HueDevice.createParser();
        final String entity = gson.toJson(testEntity);
        assertEquals("{\"value\":1.2}", entity);
    }
}
