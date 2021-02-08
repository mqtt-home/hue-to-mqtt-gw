package features.sensors;

import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.SensorType;
import io.github.zeroone3010.yahueapi.TemperatureSensor;

import java.math.BigDecimal;
import java.util.Map;

public class TemperatureSensorStub extends SensorStub implements TemperatureSensor {
    private BigDecimal temperature = BigDecimal.ZERO;

    public TemperatureSensorStub(final DeviceDescriptor device) {
        super(device);
    }

    @Override
    public SensorType getType() {
        return SensorType.TEMPERATURE;
    }

    @Override
    public void setProperties(final Map<String, String> properties) {
        super.setProperties(properties);

        this.temperature = getBigDecimal(properties, "temperature", this.temperature);
    }

    @Override
    public BigDecimal getDegreesCelsius() {
        return this.temperature;
    }
}
