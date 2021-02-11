package features.steps;

import de.rnd7.huemqtt.hue.AmbientLightSensorDevice;
import de.rnd7.huemqtt.hue.HueService;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AmbientStepDefinitions {

    @Then("I expect the ambient sensor {string} to have daylight time.")
    public void expectAmbientDaylight(final String deviceId) {
        final AmbientLightSensorDevice sensor = HueService.get().getDevice(deviceId, AmbientLightSensorDevice.class);
        assertTrue(sensor.getMessage().isDaylight());
    }

    @Then("I expect the ambient sensor {string} to not have daylight time.")
    public void expectAmbientNotDaylight(final String deviceId) {
        final AmbientLightSensorDevice sensor = HueService.get().getDevice(deviceId, AmbientLightSensorDevice.class);
        assertFalse(sensor.getMessage().isDaylight());
    }

    @Then("I expect the ambient sensor {string} to be not dark.")
    public void expectAmbientNotDark(final String deviceId) {
        final AmbientLightSensorDevice sensor = HueService.get().getDevice(deviceId, AmbientLightSensorDevice.class);
        assertFalse(sensor.getMessage().isDark());
    }

    @Then("I expect the ambient sensor {string} to be dark.")
    public void expectAmbientDark(final String deviceId) {
        final AmbientLightSensorDevice sensor = HueService.get().getDevice(deviceId, AmbientLightSensorDevice.class);
        assertTrue(sensor.getMessage().isDark());
    }

}
