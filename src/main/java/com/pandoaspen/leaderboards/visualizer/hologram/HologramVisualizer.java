package com.pandoaspen.leaderboards.visualizer.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.pandoaspen.leaderboards.LeaderboardsPlugin;
import com.pandoaspen.leaderboards.config.visualizers.ProviderVisualizerConfig;
import com.pandoaspen.leaderboards.config.visualizers.VisualizerConfig;
import com.pandoaspen.leaderboards.providers.dataproviders.IDataProvider;
import com.pandoaspen.leaderboards.providers.registry.PlayerScore;
import com.pandoaspen.leaderboards.visualizer.AbstractVisualizer;
import org.bukkit.Location;

import java.util.List;

public class HologramVisualizer extends AbstractVisualizer {

    private long nextRunTime;

    private Hologram hologram;

    private List<ProviderVisualizerConfig> providerVisualizerConfigs;
    int visualizerIndex = 0;

    public HologramVisualizer(LeaderboardsPlugin plugin, VisualizerConfig visualizerConfig) {
        super(plugin, visualizerConfig);
    }

    @Override
    public void start() {
        this.providerVisualizerConfigs = getVisualizerConfig().getProviders();
        Location location = getVisualizerConfig().getLocation();
        hologram = HologramsAPI.createHologram(getPlugin(), location);
    }

    @Override
    public void stop() {
        hologram.delete();
    }

    @Override
    public void update(long currentTime) {
        if (nextRunTime > currentTime) return;
        nextRunTime = currentTime + getVisualizerConfig().getRotatePeriod().getMillis();

        ProviderVisualizerConfig providerVisualizerConfig = providerVisualizerConfigs.get(visualizerIndex);
        visualizerIndex = (visualizerIndex + 1) % (providerVisualizerConfigs.size());


        long since = providerVisualizerConfig.getDuration().getMillis();
        int max = providerVisualizerConfig.getRows();

        IDataProvider dataProvider = getPlugin().getProviderManager().getProvider(providerVisualizerConfig.getName());


        if (dataProvider == null) {
            System.out.println(String.format("Data provider %s is not registered (%s)", providerVisualizerConfig.getName(), getPlugin().getProviderManager().getProviderNames()));
            return;
        }

        List<PlayerScore> top = dataProvider.getTop(since, max);

        hologram.clearLines();

        hologram.appendTextLine(providerVisualizerConfig.getTitle());

        String scoreFormat = dataProvider.getProviderConfig().getScoreFormat();

        String rowFormat = providerVisualizerConfig.getRowFormat();
        for (int i = 0; i < providerVisualizerConfig.getRows(); i++) {
            PlayerScore playerScore = top.get(i);
            String line = rowFormat;
            line = line.replaceAll("\\{rank\\}", Integer.toString(i + 1));
            line = line.replaceAll("\\{playername\\}", playerScore.getName());
            line = line.replaceAll("\\{score\\}", String.format(scoreFormat, playerScore.getValue()));
            hologram.appendTextLine(line);
        }

        ProviderVisualizerConfig nextProviderConf = providerVisualizerConfigs.get(visualizerIndex);
        long nextTime = nextProviderConf.getDuration().getMillis();
        //        getPlugin().getProviderManager().getProvider(nextProviderConf.getName()).prepareNextTop(nextTime, max);
    }


}
