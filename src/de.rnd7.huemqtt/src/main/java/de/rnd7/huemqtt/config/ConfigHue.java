package de.rnd7.huemqtt.config;

import com.google.gson.annotations.SerializedName;

public class ConfigHue {
    @SerializedName("host")
    private String host;
    @SerializedName("api-key")
    private String apiKey;
    @SerializedName("port")
    private int port = 443;

    public String getHost() {
        return this.host;
    }

    public ConfigHue setHost(final String host) {
        this.host = host;
        return this;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public ConfigHue setApiKey(final String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public ConfigHue setPort(final int port) {
        this.port = port;
        return this;
    }

    public int getPort() {
        return this.port;
    }
}
