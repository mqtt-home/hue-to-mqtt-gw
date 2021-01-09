package de.rnd7.huemqtt;

import de.rnd7.huemqtt.config.Config;
import de.rnd7.huemqtt.config.ConfigParser;
import de.rnd7.huemqtt.hue.HueService;
import de.rnd7.huemqtt.mqtt.GwMqttClient;
import io.github.zeroone3010.yahueapi.Hue;
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
            Events.register(this);
            final GwMqttClient client = new GwMqttClient(config, Events.getBus());

            Events.register(client);

            final HueService service = new HueService(
                new Hue(config.getHue().getHost(), config.getHue().getApiKey()),
                config.getMqtt().getTopic()).start();

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
            new Main(ConfigParser.parse(new File(args[0])));
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
