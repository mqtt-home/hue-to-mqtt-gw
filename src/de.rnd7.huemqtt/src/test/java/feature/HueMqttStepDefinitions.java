package feature;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rnd7.huemqtt.hue.DaylightSensorDevice;
import de.rnd7.huemqtt.hue.HueService;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.PublishMessage;
import feature.sensors.DaylightSensorStub;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HueMqttStepDefinitions {
    private ObjectMapper objectMapper = new ObjectMapper();
    private HueService hue;
    private HueAbstractionStub hueTestStub;
    private PublishedMessageListener messages;

    @After
    public void shutdown() {
        HueService.shutdown();
        Events.unregister(messages);
    }

    @Before
    public void init() {
        Events.getInstance().syncBus();
        this.messages = new PublishedMessageListener();
        Events.register(messages);
    }

    @Given("I have a hue bridge")
    public void i_have_a_hue_bridge() {
    }
    @Then("I expect this to work")
    public void i_expect_this_to_work() {
    }

    @Given("I have a hue bridge with the following devices:")
    public void withDevices(DataTable dataTable) {
        final List<Map<String,String>> values = dataTable.asMaps(String.class, String.class);
        final List<DeviceDescriptor> devices = values.stream().map(i -> objectMapper.convertValue(i, DeviceDescriptor.class))
            .collect(Collectors.toList());

        this.hueTestStub = new HueAbstractionStub();

        for (final DeviceDescriptor device : devices) {
            if (device.getType().equals(DeviceDescriptor.DeviceType.daylight)) {
                this.hueTestStub.addDaylightSensor(new DaylightSensorStub(device));
            }
        }

        this.hue = HueService.start(this.hueTestStub, "hue");
    }

    @When("device {string} has the following properties:")
    public void setProperties(String deviceId, DataTable dataTable) {
        final Map<String, String> properties = dataTable.asMap(String.class, String.class);

        final DaylightSensorStub device = (DaylightSensorStub) this.hueTestStub.getDevice(deviceId);
        device.setProperties(properties);

        this.hue.poll();
    }

    @Then("I expect {string} to have daylight time.")
    public void expectItIsDaylight(String deviceId) {
        final DaylightSensorDevice sensor = hue.getDevice(deviceId, DaylightSensorDevice.class);
        assertTrue(sensor.getMessage().isDaylight());
    }

    @Then("I expect {string} to not have daylight time.")
    public void expectItIsNotDaylight(String deviceId) {
        final DaylightSensorDevice sensor = hue.getDevice(deviceId, DaylightSensorDevice.class);
        assertFalse(sensor.getMessage().isDaylight());
    }

    @Then("I expect the following message on topic {string}:")
    public void expectMessageOnTopic(String topic, String message) {
        final List<PublishMessage> messages = this.messages.getMessages().stream()
            .filter(m -> m.getTopic().equals(topic))
            .collect(Collectors.toList());

        assertFalse(messages.isEmpty());

        final PublishMessage publishMessage = messages.get(messages.size() - 1);
        assertEquals(message, publishMessage.getMessage());
    }
}
