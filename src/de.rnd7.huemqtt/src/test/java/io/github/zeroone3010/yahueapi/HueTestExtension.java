package io.github.zeroone3010.yahueapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class HueTestExtension implements BeforeEachCallback, AfterEachCallback {
    public static final String API_KEY = "abcd1234";
    private static final String API_BASE_PATH = "/api/" + API_KEY + "/";

    final WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicHttpsPort().httpDisabled(true));

    @Override
    public void beforeEach(final ExtensionContext extensionContext) throws Exception {
        this.wireMockServer.start();
        init();
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws Exception {
        this.wireMockServer.stop();
    }

    public int getPort() {
        return this.wireMockServer.httpsPort();
    }

    public void init() {
        final String hueRoot = readFile("hueRoot.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(hueRoot);
            this.wireMockServer.stubFor(get(API_BASE_PATH).willReturn(okJson(hueRoot)));
            mockIndividualGetResponse(jsonNode, "lights", "100");
            mockIndividualGetResponse(jsonNode, "lights", "101");
            mockIndividualGetResponse(jsonNode, "lights", "200");
            mockIndividualGetResponse(jsonNode, "lights", "300");
            mockIndividualGetResponse(jsonNode, "lights", "400");
            mockIndividualGetResponse(jsonNode, "sensors", "1");
            mockIndividualGetResponse(jsonNode, "sensors", "4");
            mockIndividualGetResponse(jsonNode, "sensors", "15");
            mockIndividualGetResponse(jsonNode, "sensors", "16");
            mockIndividualGetResponse(jsonNode, "sensors", "17");
            mockIndividualGetResponse(jsonNode, "sensors", "20");
            mockIndividualGetResponse(jsonNode, "sensors", "99");
            mockIndividualGetResponse(jsonNode, "groups", "1");
            mockIndividualGetResponse(jsonNode, "groups", "2");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void mockIndividualGetResponse(final JsonNode hueRoot, final String itemClass, final String id) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String json = objectMapper.writeValueAsString(hueRoot.get(itemClass).get(id));
        this.wireMockServer.stubFor(get(API_BASE_PATH + itemClass + "/" + id).willReturn(okJson(json)));
    }

    private String readFile(final String fileName) {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource(fileName).getFile());
        try {
            return Files.lines(file.toPath()).collect(Collectors.joining());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
