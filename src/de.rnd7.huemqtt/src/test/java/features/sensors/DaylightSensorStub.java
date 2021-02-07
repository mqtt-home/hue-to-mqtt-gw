package features.sensors;

import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.DaylightSensor;
import io.github.zeroone3010.yahueapi.SensorType;

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
        return this.daylight;
    }

    @Override
    public void setProperties(final Map<String, String> properties) {
        super.setProperties(properties);

        this.daylight = getBoolean(properties, "daylight", this.daylight);
    }

}
