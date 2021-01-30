package de.rnd7.huemqtt.effects;

public class MessageData {
    private final String message;
    private final Object expectedData;

    public MessageData(final String message, final Object expectedData) {
        this.message = message;
        this.expectedData = expectedData;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getExpectedData() {
        return this.expectedData;
    }

    @Override
    public String toString() {
        return "MessageData{" +
            "message='" + this.message + '\'' +
            '}';
    }
}
