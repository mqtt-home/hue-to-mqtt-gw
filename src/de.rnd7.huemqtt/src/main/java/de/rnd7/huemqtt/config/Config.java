package de.rnd7.huemqtt.config;

import de.rnd7.mqttgateway.config.ConfigMqtt;

public class Config {

    private ConfigMqtt mqtt;
    private ConfigHue hue;

    public ConfigMqtt getMqtt() {
        return mqtt;
    }

    public ConfigHue getHue() {
        return hue;
    }
}
