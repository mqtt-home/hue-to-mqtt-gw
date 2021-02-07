package feature;

import com.google.common.eventbus.Subscribe;
import de.rnd7.mqttgateway.PublishMessage;

import java.util.ArrayList;
import java.util.List;

public class PublishedMessageListener {

    private List<PublishMessage> messages = new ArrayList<>();

    @Subscribe
    public void onMessage(final PublishMessage message) {
        messages.add(message);
    }

    public void clear() {
        messages.clear();
    }

    public List<PublishMessage> getMessages() {
        return messages;
    }
}
