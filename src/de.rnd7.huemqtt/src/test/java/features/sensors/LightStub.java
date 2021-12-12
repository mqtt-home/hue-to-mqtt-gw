package features.sensors;

import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.LightType;
import io.github.zeroone3010.yahueapi.State;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public abstract class LightStub implements Light {
    private final DeviceDescriptor device;
    private ZonedDateTime lastUpdated = ZonedDateTime
        .of(LocalDate.of(2021, 01, 01),
            LocalTime.NOON, ZoneId.of("Europe/Berlin"));

    public LightStub(final DeviceDescriptor device) {
        this.device = device;
    }

    @Override
    public String getName() {
        return this.device.getId();
    }

    @Override
    public String getId() {
        return this.device.getId();
    }

    @Override
    public void turnOn() {

    }

    @Override
    public void turnOff() {

    }

    @Override
    public boolean isOn() {
        return false;
    }

    @Override
    public boolean isReachable() {
        return true;
    }

    @Override
    public void setBrightness(final int brightness) {

    }

    @Override
    public LightType getType() {
        return null;
    }

    @Override
    public void setState(final State state) {

    }

    @Override
    public State getState() {
        return null;
    }


    public void setProperties(final Map<String, String> properties) {
        final String lastUpdated = properties.get("last-updated");
        if (lastUpdated != null) {
            this.lastUpdated = ZonedDateTime.parse(lastUpdated);
        }
    }

    protected String getString(final Map<String, String> properties, final String key, final String defaultValue) {
        final String value = properties.get(key);
        if (value != null) {
            return value;
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

    protected BigDecimal getBigDecimal(final Map<String, String> properties, final String key, final BigDecimal defaultValue) {
        final String value = properties.get(key);
        if (value != null) {
            return new BigDecimal(value);
        }
        else {
            return defaultValue;
        }
    }
}
