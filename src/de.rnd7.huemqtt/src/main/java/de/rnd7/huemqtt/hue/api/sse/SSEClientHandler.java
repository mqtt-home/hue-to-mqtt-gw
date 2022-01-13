package de.rnd7.huemqtt.hue.api.sse;

import com.google.gson.Gson;
import de.rnd7.huemqtt.hue.api.sse.model.HueEvent;
import de.rnd7.huemqtt.hue.api.sse.model.HueEventData;
import de.rnd7.huemqtt.hue.api.sse.model.HueEventDataType;
import de.rnd7.huemqtt.hue.api.sse.model.HueEventType;
import netscape.javascript.JSException;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class SSEClientHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SSEClientHandler.class);
    public static final int TIMEOUT = 1000;
    public static final int MAX_RETRY_CTR = 10_000 / TIMEOUT;

    private final BlockingQueue<Event> events;
    private final CloseableHttpAsyncClient asyncClient;
    private final Consumer<Object> consumer;

    SSEClientHandler(final BlockingQueue<Event> events, final CloseableHttpAsyncClient asyncClient, final Consumer<Object> consumer) {
        this.events = events;
        this.asyncClient = asyncClient;
        this.consumer = consumer;
    }

    public void run() throws Exception {
        int noMessageCtr = 0;

        while (true) {
            final Event event = events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
            if (event != null) {
                noMessageCtr = 0;

                handleEvent(event);
            }
            else if (noMessageCtr >= MAX_RETRY_CTR || !asyncClient.isRunning()) {
                // No message for more than 10s (not even ping) - try reconnect.
                close();
                break;
            }
            else {
                noMessageCtr++;
            }
        }
    }

    private void close() throws IOException {
        asyncClient.close();
        events.stream().close();
    }

    private void handleEvent(final Event event) {
        try {
            if (event.getData() == null || event.getData().isEmpty()) {
                return;
            }
            final Gson gson = new Gson();
            HueEvent[] events = gson.fromJson(event.getData(), HueEvent[].class);
            for (final HueEvent hueEvent : events) {
                handleHueEvent(hueEvent);
            }
        }
        catch (JSONException e) {
            LOGGER.debug("Cannot parse event as JSON: {}", event.getData(), e);
        }
    }

    private void handleHueEvent(final HueEvent event) {
        if (event.getType() == HueEventType.UPDATE) {
            LOGGER.info("SSE Update: {} {}", event.getId(), event.getType());

            for (final HueEventData data : event.getData()) {
                if (data.getType() == null) {
                    System.out.println("halt");
                }

                LOGGER.info("  {} {}", data.getId(), data.getType());
            }
        }

//        final HueEventType type = event.getEnum(HueEventType.class, "type");
//        if (type == HueEventType.UPDATE) {
//            final String id = event.getString("id");
//            final JSONArray data = event.getJSONArray("data");
//            LOGGER.info("SSE Update: {}", id, data);
//        }
    }

}
