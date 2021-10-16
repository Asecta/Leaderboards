package com.pandoaspen.leaderboards.visualizer;

import com.pandoaspen.leaderboards.LeaderboardsPlugin;
import com.pandoaspen.leaderboards.config.visualizers.VisualizerConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class AbstractVisualizer implements IVisualizer {

    private final LeaderboardsPlugin plugin;
    private final VisualizerConfig visualizerConfig;

}
