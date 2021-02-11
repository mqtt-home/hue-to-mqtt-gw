package features.steps;

import de.rnd7.huemqtt.hue.DaylightSensorDevice;
import de.rnd7.huemqtt.hue.HueService;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DaylightStepDefinitions {

    @Then("I expect {string} to have daylight time.")
    public void expectItIsDaylight(final String deviceId) {
        final DaylightSensorDevice sensor = HueService.get().getDevice(deviceId, DaylightSensorDevice.class);
        assertTrue(sensor.getMessage().isDaylight());
    }

    @Then("I expect {string} to not have daylight time.")
    public void expectItIsNotDaylight(final String deviceId) {
        final DaylightSensorDevice sensor = HueService.get().getDevice(deviceId, DaylightSensorDevice.class);
        assertFalse(sensor.getMessage().isDaylight());
    }


}
