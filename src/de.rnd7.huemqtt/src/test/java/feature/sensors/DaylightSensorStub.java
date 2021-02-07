package feature.sensors;

import feature.DeviceDescriptor;
import feature.DeviceProperty;
import io.github.zeroone3010.yahueapi.DaylightSensor;
import io.github.zeroone3010.yahueapi.SensorType;

import java.util.List;
import java.util.Map;

public class DaylightSensorStub extends SensorStub implements DaylightSensor {
    private boolean daylight = false;

    public DaylightSensorStub(final DeviceDescriptor device) {
        super(device);
    }

    @Override
    public SensorType getType() {
        return SensorType.DAYLIGHT;
    }

    @Override
    public boolean isDaylightTime() {
        return daylight;
    }

    @Override
    public void setProperties(final Map<String, String> properties) {
        super.setProperties(properties);

        final String daylightProp = properties.get("daylight");
        if (daylightProp != null) {
            this.daylight = Boolean.parseBoolean(daylightProp);
        }
    }
}
