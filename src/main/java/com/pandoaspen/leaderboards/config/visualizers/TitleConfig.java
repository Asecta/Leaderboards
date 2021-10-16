package com.pandoaspen.leaderboards.config.visualizers;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Location;

@Getter
public class TitleConfig {
    @SerializedName("enabled") private boolean enabled;
    @SerializedName("location") private Location location;
}