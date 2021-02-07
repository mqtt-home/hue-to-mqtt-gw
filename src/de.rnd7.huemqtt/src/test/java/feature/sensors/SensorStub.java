package feature.sensors;

import feature.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.Sensor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

abstract class SensorStub implements Sensor {
    private final DeviceDescriptor device;
    private ZonedDateTime lastUpdated = ZonedDateTime
        .of(LocalDate.of(2021, 01, 01),
            LocalTime.NOON, ZoneId.of("Europe/Berlin"));

    public SensorStub(final DeviceDescriptor device) {
        this.device = device;
    }

    @Override
    public String getName() {
        return device.getId();
    }

    @Override
    public String getProductName() {
        return device.getType().name();
    }

    @Override
    public String getId() {
        return device.getId();
    }

    @Override
    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setProperties(final Map<String, String> properties) {
        final String lastUpdated = properties.get("last-updated");
        if (lastUpdated != null) {
            this.lastUpdated = ZonedDateTime.parse(lastUpdated);
        }
    }
}
