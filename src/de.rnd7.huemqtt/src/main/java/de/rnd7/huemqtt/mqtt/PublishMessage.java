package de.rnd7.huemqtt.mqtt;

public class PublishMessage {
    private final String topic;
    private final String message;

    public PublishMessage(final String topic, final String message) {
        this.topic = topic;
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public String getMessage() {
        return message;
    }

    public static String cleanTopic(final String topic) {
        return topic
            .replace(" ", "-").toLowerCase()
            .replace("ä", "ae")
            .replace("ö", "oe")
            .replace("ü", "ue");
    }
}
