package de.rnd7.huemqtt.hue.api.sse;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SSEClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SSEClient.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    private CloseableHttpAsyncClient asyncClient;

    BlockingQueue<Event> subscribe(final String bridgeIp, final String apiKey) throws Exception {
        LOGGER.debug("Subscribe SSE");
        final SSLContextBuilder builder = SSLContexts.custom();
        builder.loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(final X509Certificate[] chain, final String authType)
                throws CertificateException {
                return true;
            }
        });
        final SSLContext sslContext = builder.build();
        final SchemeIOSessionStrategy sslioSessionStrategy = new SSLIOSessionStrategy(sslContext,
            new HostnameVerifier(){
                @Override
                public boolean verify(final String hostname, final SSLSession session) {
                    return true; // TODO as of now allow all hostnames
                }
            });
        final Registry<SchemeIOSessionStrategy> sslioSessionRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create().register("https", sslioSessionStrategy).build();
        final PoolingNHttpClientConnectionManager ncm = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor(), sslioSessionRegistry);
        asyncClient = HttpAsyncClients.custom().setConnectionManager(ncm).build();

        asyncClient.start();
        final SseRequest request = new SseRequest(String.format("https://%s/eventstream/clip/v2", bridgeIp));
        request.setHeader("hue-application-key", apiKey);
        request.setHeader("Accept", "text/event-stream");

        return new ApacheHttpSseClient(asyncClient, executor)
            .execute(request)
            .get(10, TimeUnit.SECONDS)
            .getEntity()
            .getEvents();
    }

    void shutdown() {
        closeClient();

        executor.shutdown();
    }

    void closeClient() {
        if (asyncClient != null) {
            try {
                asyncClient.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    boolean isRunning() {
        return asyncClient.isRunning();
    }

    public void start(final String bridgeIp, final String apiKey) {
        while (true) { // NOSONAR
            try {
                final BlockingQueue<Event> events = subscribe(bridgeIp, apiKey);
                //listener.state(MieleAPIState.connected);

                new SSEClientHandler(events, asyncClient, (o) -> {}).run();
            } catch (Exception e) {
                //listener.state(MieleAPIState.disconnected);
                LOGGER.error(e.getMessage(), e);
/*
                if (!api.waitReconnect()) {
                    shutdown();
                    return;
                }*/
            }
        }
    }
}
