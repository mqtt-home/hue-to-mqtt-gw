package features.sensors;

import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.Sensor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public abstract class SensorStub implements Sensor {
    private final DeviceDescriptor device;
    private ZonedDateTime lastUpdated = ZonedDateTime
        .of(LocalDate.of(2021, 01, 01),
            LocalTime.NOON, ZoneId.of("Europe/Berlin"));

    public SensorStub(final DeviceDescriptor device) {
        this.device = device;
    }

    @Override
    public String getName() {
        return this.device.getId();
    }

    @Override
    public String getProductName() {
        return this.device.getType().name();
    }

    @Override
    public String getId() {
        return this.device.getId();
    }

    @Override
    public ZonedDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    public void setProperties(final Map<String, String> properties) {
        final String lastUpdated = properties.get("last-updated");
        if (lastUpdated != null) {
            this.lastUpdated = ZonedDateTime.parse(lastUpdated);
        }
    }

    protected boolean getBoolean(final Map<String, String> properties, final String key, final boolean defaultValue) {
        final String value = properties.get(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        else {
            return defaultValue;
        }
    }

    protected int getInt(final Map<String, String> properties, final String key, final int defaultValue) {
        final String value = properties.get(key);
        if (value != null) {
            return Integer.parseInt(value);
        }
        else {
            return defaultValue;
        }
    }
}
