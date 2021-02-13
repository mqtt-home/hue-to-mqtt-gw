package integration;

import com.hivemq.testcontainer.junit5.HiveMQTestContainerExtension;
import de.rnd7.huemqtt.config.Config;
import io.github.zeroone3010.yahueapi.HueTestExtension;

public class ConfigurationUtil {

    private ConfigurationUtil() {
    }

    public static Config createConfig(final HiveMQTestContainerExtension hiveMQ, final HueTestExtension hue) {
        final Config config = new Config();

        config.getHue()
            .setHost("localhost")
            .setPort(hue.getPort())
            .setApiKey(HueTestExtension.API_KEY);

        config.getMqtt().setUrl(String.format("tcp://%s:%s",
            hiveMQ.getHost(),
            hiveMQ.getMqttPort()));

        return config;
    }

}
