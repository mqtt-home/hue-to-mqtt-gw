package features;

import com.google.common.eventbus.Subscribe;
import de.rnd7.mqttgateway.PublishMessage;

import java.util.ArrayList;
import java.util.List;

public class PublishedMessageListener {

    private final List<PublishMessage> messages = new ArrayList<>();

    @Subscribe
    public void onMessage(final PublishMessage message) {
        this.messages.add(message);
    }

    public void clear() {
        this.messages.clear();
    }

    public List<PublishMessage> getMessages() {
        return this.messages;
    }
}
