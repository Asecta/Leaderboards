package com.pandoaspen.leaderboards.config.visualizers;


import com.google.gson.annotations.SerializedName;
import com.pandoaspen.leaderboards.utils.Duration;
import com.pandoaspen.leaderboards.visualizer.VisualizerType;
import lombok.Getter;

import java.util.List;

@Getter
public class VisualizerConfig {

    @SerializedName("type") private VisualizerType type;
    @SerializedName("title") private TitleConfig title;

    @SerializedName("providers") private List<ProviderVisualizerConfig> providers;
    @SerializedName("rotate-period") private Duration rotatePeriod;
    @SerializedName("watch-player") private boolean watchPlayer;
    @SerializedName("watch-distance") private int watchDistance;
    @SerializedName("npcs") private List<NPCConfig> npcs;

}