package de.rnd7.huemqtt.hue;

import de.rnd7.mqttgateway.Message;

public abstract class Device {

    private final String topic;
    private final String id;

    protected Device(final String topic, final String id) {
        this.topic = topic;
        this.id = id;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getId() {
        return this.id;
    }

    public abstract boolean apply(final Message message);

    protected boolean onMessage(final Message message) {
        return false;
    }

    @Override
    public String toString() {
        return "Device{" +
                "topic='" + this.topic + '\'' +
                '}';
    }

}
