package features;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rnd7.huemqtt.hue.AmbientLightSensorDevice;
import de.rnd7.huemqtt.hue.DaylightSensorDevice;
import de.rnd7.huemqtt.hue.HueService;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.PublishMessage;
import features.sensors.AmbientLightSensorStub;
import features.sensors.DaylightSensorStub;
import features.sensors.SensorStub;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HueMqttStepDefinitions {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private HueService hue;
    private HueAbstractionStub hueTestStub;
    private PublishedMessageListener messages;

    @After
    public void shutdown() {
        HueService.shutdown();
        Events.unregister(this.messages);
    }

    @Before
    public void init() {
        Events.getInstance().syncBus();
        this.messages = new PublishedMessageListener();
        Events.register(this.messages);
    }

    @Given("I have a hue bridge")
    public void i_have_a_hue_bridge() {
    }
    @Then("I expect this to work")
    public void i_expect_this_to_work() {
    }

    @Given("I have a hue bridge with the following devices:")
    public void withDevices(final DataTable dataTable) {
        final List<Map<String,String>> values = dataTable.asMaps(String.class, String.class);
        final List<DeviceDescriptor> devices = values.stream().map(i -> this.objectMapper.convertValue(i, DeviceDescriptor.class))
            .collect(Collectors.toList());

        this.hueTestStub = new HueAbstractionStub();

        for (final DeviceDescriptor device : devices) {
            switch (device.getType()) {
                case daylight:
                    this.hueTestStub.addSensor(new DaylightSensorStub(device));
                    break;
                case ambient:
                    this.hueTestStub.addSensor(new AmbientLightSensorStub(device));
                    break;
            }
        }

        this.hue = HueService.start(this.hueTestStub, "hue");
    }

    @When("device {string} has the following properties:")
    public void setProperties(final String deviceId, final DataTable dataTable) {
        final Map<String, String> properties = dataTable.asMap(String.class, String.class);

        this.hueTestStub.getSensor(deviceId, SensorStub.class).setProperties(properties);

        this.hue.poll();
    }

    @Then("I expect {string} to have daylight time.")
    public void expectItIsDaylight(final String deviceId) {
        final DaylightSensorDevice sensor = this.hue.getDevice(deviceId, DaylightSensorDevice.class);
        assertTrue(sensor.getMessage().isDaylight());
    }

    @Then("I expect {string} to not have daylight time.")
    public void expectItIsNotDaylight(final String deviceId) {
        final DaylightSensorDevice sensor = this.hue.getDevice(deviceId, DaylightSensorDevice.class);
        assertFalse(sensor.getMessage().isDaylight());
    }

    @Then("I expect the ambient sensor {string} to have daylight time.")
    public void expectAmbientDaylight(final String deviceId) {
        final AmbientLightSensorDevice sensor = this.hue.getDevice(deviceId, AmbientLightSensorDevice.class);
        assertTrue(sensor.getMessage().isDaylight());
    }

    @Then("I expect the ambient sensor {string} to not have daylight time.")
    public void expectAmbientNotDaylight(final String deviceId) {
        final AmbientLightSensorDevice sensor = this.hue.getDevice(deviceId, AmbientLightSensorDevice.class);
        assertFalse(sensor.getMessage().isDaylight());
    }

    @Then("I expect the ambient sensor {string} to be not dark.")
    public void expectAmbientNotDark(final String deviceId) {
        final AmbientLightSensorDevice sensor = this.hue.getDevice(deviceId, AmbientLightSensorDevice.class);
        assertFalse(sensor.getMessage().isDark());
    }

    @Then("I expect the ambient sensor {string} to be dark.")
    public void expectAmbientDark(final String deviceId) {
        final AmbientLightSensorDevice sensor = this.hue.getDevice(deviceId, AmbientLightSensorDevice.class);
        assertTrue(sensor.getMessage().isDark());
    }

    @Then("I expect the following message on topic {string}:")
    public void expectMessageOnTopic(final String topic, final String message) {
        final List<PublishMessage> messages = this.messages.getMessages().stream()
            .filter(m -> m.getTopic().equals(topic))
            .collect(Collectors.toList());

        assertFalse(messages.isEmpty());

        final PublishMessage publishMessage = messages.get(messages.size() - 1);
        assertEquals(message, publishMessage.getMessage());
    }

}
