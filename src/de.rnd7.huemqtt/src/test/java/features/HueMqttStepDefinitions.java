package features;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rnd7.huemqtt.hue.HueService;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.PublishMessage;
import features.sensors.LightStub;
import features.sensors.SensorFactory;
import features.sensors.SensorStub;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @Given("I have a hue bridge with the following devices:")
    public void withDevices(final DataTable dataTable) {
        final List<Map<String,String>> values = dataTable.asMaps(String.class, String.class);
        final List<DeviceDescriptor> devices = values.stream().map(i -> this.objectMapper.convertValue(i, DeviceDescriptor.class))
            .collect(Collectors.toList());

        this.hueTestStub = new HueAbstractionStub();

        devices.stream()
            .map(SensorFactory::createSensor)
            .filter(Objects::nonNull)
            .forEach(this.hueTestStub::addSensor);

        devices.stream()
            .map(SensorFactory::createLight)
            .filter(Objects::nonNull)
            .forEach(this.hueTestStub::addLight);

        this.hue = HueService.start(this.hueTestStub, "hue");
    }

    @When("device {string} has the following properties:")
    public void setProperties(final String deviceId, final DataTable dataTable) {
        final Map<String, String> properties = dataTable.asMap(String.class, String.class);

        final Optional<LightStub> light = this.hueTestStub.getLight(deviceId, LightStub.class);
        if (light.isPresent()) {
            light.get()
                .setProperties(properties);
        }
        else {
            this.hueTestStub.getSensor(deviceId, SensorStub.class)
                .setProperties(properties);
        }

        this.hue.poll();
    }

    @Then("I expect the following message on topic {string}:")
    public void expectMessageOnTopic(final String topic, final String message) {
        final List<PublishMessage> messages = this.messages.getMessages().stream()
            .filter(m -> m.getTopic().equals(topic))
            .collect(Collectors.toList());

        assertFalse(messages.isEmpty());

        final PublishMessage publishMessage = messages.get(messages.size() - 1);
        JSONAssert.assertEquals(message, publishMessage.getMessage(), JSONCompareMode.LENIENT);
    }

}
