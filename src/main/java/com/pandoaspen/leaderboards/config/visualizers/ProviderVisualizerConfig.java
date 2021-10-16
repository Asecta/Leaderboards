package com.pandoaspen.leaderboards.config.visualizers;

import com.google.gson.annotations.SerializedName;
import com.pandoaspen.leaderboards.utils.Duration;
import lombok.Getter;

@Getter
public class ProviderVisualizerConfig {
    @SerializedName("name") private String name;
    @SerializedName("show-since") private Duration duration;
    @SerializedName("title") private String title;
    @SerializedName("npc-title") private String npcTitle;
}