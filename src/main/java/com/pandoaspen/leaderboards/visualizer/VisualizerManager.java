package com.pandoaspen.leaderboards.visualizer;

import com.pandoaspen.leaderboards.LeaderboardsPlugin;
import com.pandoaspen.leaderboards.config.visualizers.VisualizerConfig;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class VisualizerManager {

    private final LeaderboardsPlugin plugin;
    private List<IVisualizer> visualizers;

    public void loadVisualizers() {
        this.visualizers = new ArrayList<>();

        for (VisualizerConfig visualizerConfig : plugin.getLeaderboardConfig().getVisualizers()) {
            IVisualizer visualizer = visualizerConfig.getType().instantiate(plugin, visualizerConfig);
            this.visualizers.add(visualizer);
        }
    }

    public void shutdown() {
        visualizers.forEach(IVisualizer::stop);
    }

    public void startTask() {
        visualizers.forEach(IVisualizer::start);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            visualizers.forEach(visualizer -> visualizer.update(currentTime));
        }, 1, 1);
    }
}
