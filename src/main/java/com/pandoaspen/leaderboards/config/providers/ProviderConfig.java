package com.pandoaspen.leaderboards.config.providers;

import com.google.gson.annotations.SerializedName;
import com.pandoaspen.leaderboards.providers.ProviderType;
import com.pandoaspen.leaderboards.utils.Duration;
import lombok.Getter;

import java.util.List;

@Getter
public class ProviderConfig {
    @SerializedName("name") private String name;
    @SerializedName("type") private ProviderType type;
    @SerializedName("placeholder") private String placeholder;
    @SerializedName("update-period") private Duration updatePeriod;
    @SerializedName("score-format") private String scoreFormat;
    @SerializedName("update-triggers") private List<TriggerConfig> updateTriggers;
}
