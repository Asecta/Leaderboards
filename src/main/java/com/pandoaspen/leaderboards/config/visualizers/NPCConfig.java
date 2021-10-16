package com.pandoaspen.leaderboards.config.visualizers;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Location;

@Getter
public class NPCConfig {
    @SerializedName("location") private Location location;
    @SerializedName("rank") private int rank;
}