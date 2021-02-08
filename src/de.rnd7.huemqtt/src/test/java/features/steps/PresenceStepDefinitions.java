package features.steps;

import de.rnd7.huemqtt.hue.HueService;
import de.rnd7.huemqtt.hue.PresenceSensorDevice;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PresenceStepDefinitions {
    @Then("I expect {string} to have presence.")
    public void expectPresence(final String deviceId) {
        final PresenceSensorDevice sensor = HueService.get().getDevice(deviceId, PresenceSensorDevice.class);
        assertTrue(sensor.getMessage().isPresence());
    }

    @Then("I expect {string} to have no presence.")
    public void expectNoPresence(final String deviceId) {
        final PresenceSensorDevice sensor = HueService.get().getDevice(deviceId, PresenceSensorDevice.class);
        assertFalse(sensor.getMessage().isPresence());
    }
}
