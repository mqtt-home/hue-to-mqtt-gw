package de.rnd7.huemqtt;

import de.rnd7.huemqtt.config.Config;
import de.rnd7.huemqtt.hue.HueService;
import de.rnd7.huemqtt.hue.api.HueAbstractionImpl;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.GwMqttClient;
import de.rnd7.mqttgateway.config.ConfigParser;
import io.github.zeroone3010.yahueapi.Hue;
import io.github.zeroone3010.yahueapi.HueBridgeProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public Main(final Config config) {
        LOGGER.debug("Debug enabled");
        LOGGER.info("Info enabled");

        try {
            final GwMqttClient client = GwMqttClient.start(config.getMqtt()
                .setDefaultTopic("hue")
            );

            client.subscribe(config.getMqtt().getTopic() + "/light/#");
            client.online();

            final HueAbstractionImpl hue = new HueAbstractionImpl(
                new Hue(HueBridgeProtocol.HTTP,
                    config.getHue().getHost() + ":" + config.getHue().getPort(),
                    config.getHue().getApiKey()));

            final HueService service = HueService.start(hue,
                config.getMqtt().getTopic());

            Events.register(service);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void main(final String[] args) {
        if (args.length != 1) {
            LOGGER.error("Expected configuration file as argument");
            return;
        }

        try {
            new Main(ConfigParser.parse(new File(args[0]), Config.class));
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
