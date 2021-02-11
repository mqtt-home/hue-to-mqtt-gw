package de.rnd7.huemqtt.hue;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZonedDateTimeTypeAdapterTest {
    public static class TestEntity {
        private ZonedDateTime value;

        public TestEntity setValue(final ZonedDateTime value) {
            this.value = value;
            return this;
        }
    }

    @Test
    void test_read() {
        final Gson gson = HueDevice.createParser();
        final TestEntity entity = gson.fromJson("{\"value\":\"2021-01-02T12:45+01:00[Europe/Berlin]\"}", TestEntity.class);
        assertEquals(getExampleDate(), entity.value);
    }

    @Test
    void test_write() {
        final ZonedDateTime dateTime = getExampleDate();

        final TestEntity testEntity = new TestEntity().setValue(dateTime);
        final Gson gson = HueDevice.createParser();
        final String entity = gson.toJson(testEntity);
        assertEquals("{\"value\":\"2021-01-02T12:45+01:00[Europe/Berlin]\"}", entity);
    }

    @NotNull
    private ZonedDateTime getExampleDate() {
        return ZonedDateTime.of(LocalDate.of(2021, 1, 2),
            LocalTime.of(12, 45),
            ZoneId.of("Europe/Berlin"));
    }
}
