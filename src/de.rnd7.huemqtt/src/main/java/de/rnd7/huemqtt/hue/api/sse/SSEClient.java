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
        // asyncClient = HttpAsyncClients.custom().setss.createDefault();
        SSLContextBuilder builder = SSLContexts.custom();
        builder.loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
                return true;
            }
        });
        SSLContext sslContext = builder.build();
        SchemeIOSessionStrategy sslioSessionStrategy = new SSLIOSessionStrategy(sslContext,
            new HostnameVerifier(){
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;// TODO as of now allow all hostnames
                }
            });
        Registry<SchemeIOSessionStrategy> sslioSessionRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create().register("https", sslioSessionStrategy).build();
        PoolingNHttpClientConnectionManager ncm  = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor(),sslioSessionRegistry);
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
