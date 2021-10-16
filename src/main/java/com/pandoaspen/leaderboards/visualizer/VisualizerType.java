package com.pandoaspen.leaderboards.visualizer;

import com.pandoaspen.leaderboards.LeaderboardsPlugin;
import com.pandoaspen.leaderboards.config.LeaderboardConfig;
import com.pandoaspen.leaderboards.config.visualizers.VisualizerConfig;
import com.pandoaspen.leaderboards.visualizer.hologram.HologramVisualizer;
import com.pandoaspen.leaderboards.visualizer.player.PlayerVisualizer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
public enum VisualizerType {

    HOLOGRAM("HOLOGRAM", (plugin, config) -> new HologramVisualizer(plugin, config)),
    PLAYER("PLAYER", (plugin, config) -> new PlayerVisualizer(plugin, config));

    private final String name;
    private final BiFunction<LeaderboardsPlugin, VisualizerConfig, IVisualizer> instantiator;

    public IVisualizer instantiate(LeaderboardsPlugin plugin, VisualizerConfig config) {
        return instantiator.apply(plugin, config);
    }
}
