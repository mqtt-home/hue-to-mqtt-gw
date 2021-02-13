package integration;

import com.hivemq.testcontainer.junit5.HiveMQTestContainerExtension;
import de.rnd7.huemqtt.Main;
import de.rnd7.huemqtt.hue.HueService;
import de.rnd7.huemqtt.hue.LightDevice;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.Message;
import io.github.zeroone3010.yahueapi.HueTestExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class IntegrationTest {
    @RegisterExtension
    public final HiveMQTestContainerExtension hiveMQ
        = new HiveMQTestContainerExtension("hivemq/hivemq-ce", "2020.6");

    @RegisterExtension
    public final HueTestExtension hue = new HueTestExtension();

    @RegisterExtension
    public final MqttSubscriptionExtension subscription = new MqttSubscriptionExtension(this.hiveMQ);

    @AfterEach
    public void tearDown(){
        HueService.shutdown();
    }

    @Test
    void test_bridge_info() {
        Main.start(ConfigurationUtil.createConfig(this.hiveMQ, this.hue));

        final Message message = this.subscription.awaitTopic("hue/bridge/state");
        assertEquals("online", message.getRaw());
    }

    @Test
    void test_get_light() {
        Main.start(ConfigurationUtil.createConfig(this.hiveMQ, this.hue));

        final LightDevice light = HueService.get()
            .getDevice("hue/light/living-room/lr-1", LightDevice.class);

        Events.post(new Message(light.getGetTopic(), "{\"state\": \"\"}"));

        final Message message = this.subscription.awaitTopic(light.getTopic());
        JSONAssert.assertEquals("{\"state\":\"OFF\",\"brightness\":254,\"color\":{\"x\":0.3689,\"y\":0.3719}}",
            message.getRaw(),
            JSONCompareMode.NON_EXTENSIBLE);
    }
}
