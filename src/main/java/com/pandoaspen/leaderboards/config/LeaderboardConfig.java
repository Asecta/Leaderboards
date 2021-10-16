package com.pandoaspen.leaderboards.config;

import com.google.gson.annotations.SerializedName;
import com.pandoaspen.leaderboards.config.providers.ProviderConfig;
import com.pandoaspen.leaderboards.config.visualizers.VisualizerConfig;
import lombok.Getter;

import java.util.List;

@Getter
public class LeaderboardConfig {

    @SerializedName("data-providers") private List<ProviderConfig> dataProviders;

    @SerializedName("visualizers") private List<VisualizerConfig> visualizers;


}
