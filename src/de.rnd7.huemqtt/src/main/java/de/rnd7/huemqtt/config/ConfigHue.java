package de.rnd7.huemqtt.config;

import com.google.gson.annotations.SerializedName;

public class ConfigHue {
    @SerializedName("host")
    private String host;
    @SerializedName("api-key")
    private String apiKey;

    public String getHost() {
        return host;
    }

    public String getApiKey() {
        return apiKey;
    }

}
