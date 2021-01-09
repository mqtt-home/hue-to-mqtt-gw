package de.rnd7.huemqtt.config;

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
