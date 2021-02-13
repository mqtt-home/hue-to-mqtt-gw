package de.rnd7.huemqtt.config;

import de.rnd7.mqttgateway.config.ConfigMqtt;

public class Config {

    private final ConfigMqtt mqtt = new ConfigMqtt();
    private final ConfigHue hue = new ConfigHue();

    public ConfigMqtt getMqtt() {
        return this.mqtt;
    }

    public ConfigHue getHue() {
        return this.hue;
    }
}
