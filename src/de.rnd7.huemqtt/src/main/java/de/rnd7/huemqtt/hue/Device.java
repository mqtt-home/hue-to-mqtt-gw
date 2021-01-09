package de.rnd7.huemqtt.hue;

import de.rnd7.huemqtt.mqtt.Message;

public abstract class Device {

    private final String topic;
    private final String id;

    public Device(final String topic, final String id) {
        this.topic = topic;
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public String getId() {
        return id;
    }

    public boolean apply(final Message message) {
        if (message.getTopic().equals(topic)) {
            return onMessage(message);
        }
        return false;
    }

    protected abstract boolean onMessage(final Message message);

    @Override
    public String toString() {
        return "Device{" +
                "topic='" + topic + '\'' +
                '}';
    }

}
