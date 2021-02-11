package features.steps;

import de.rnd7.huemqtt.hue.HueService;
import de.rnd7.huemqtt.hue.TemperatureSensorDevice;
import io.cucumber.java.en.Then;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemperatureStepDefinitions {
    @Then("I expect {string} to have {string} °C")
    public void expectDegreeCelsius(final String deviceId, final String temperature) {
        final TemperatureSensorDevice sensor = HueService.get().getDevice(deviceId, TemperatureSensorDevice.class);
        assertEquals(new BigDecimal(temperature), sensor.getMessage().getTemperature());
    }
}
