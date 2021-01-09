package de.rnd7.huemqtt.mqtt;

public class Message {
    private final String raw;
    private final String topic;

    public Message(final String topic, final String raw) {
        this.topic = topic;
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

    public String getTopic() {
        return topic;
    }
}
