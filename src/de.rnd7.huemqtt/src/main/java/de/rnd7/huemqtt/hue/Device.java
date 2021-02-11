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

    public boolean apply(final Message message) {
        if (message.getTopic().equals(this.topic)) {
            return onMessage(message);
        }
        return false;
    }

    protected abstract boolean onMessage(final Message message);

    @Override
    public String toString() {
        return "Device{" +
                "topic='" + this.topic + '\'' +
                '}';
    }

}
