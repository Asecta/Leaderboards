package com.pandoaspen.leaderboards;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pandoaspen.leaderboards.commands.LeaderboardCommand;
import com.pandoaspen.leaderboards.config.LeaderboardConfig;
import com.pandoaspen.leaderboards.providers.ProviderManager;
import com.pandoaspen.leaderboards.utils.ConfigLoader;
import com.pandoaspen.leaderboards.utils.Duration;
import com.pandoaspen.leaderboards.utils.gsonadapter.ColoredStringAdapter;
import com.pandoaspen.leaderboards.utils.gsonadapter.DurationTypeAdapter;
import com.pandoaspen.leaderboards.utils.gsonadapter.LocationTypeAdapter;
import com.pandoaspen.leaderboards.visualizer.VisualizerManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class LeaderboardsPlugin extends JavaPlugin {

    private PaperCommandManager commandManager;

    @Getter private LeaderboardConfig leaderboardConfig;

    @Getter private ProviderManager providerManager;
    @Getter private VisualizerManager visualizerManager;

    @Override
    public void onEnable() {
        loadConfigs();
        loadManagers();
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        providerManager.saveProviders();
        visualizerManager.shutdown();
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
    }

    private void loadConfigs() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(String.class, new ColoredStringAdapter())
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .setPrettyPrinting().create();
        this.leaderboardConfig = ConfigLoader.load(this, gson, "config.yml", LeaderboardConfig.class);
    }

    private void loadManagers() {
        this.providerManager = new ProviderManager(this);
        this.providerManager.loadProviders();
        this.providerManager.startTask();

        this.visualizerManager = new VisualizerManager(this);
        this.visualizerManager.loadVisualizers();

        this.visualizerManager.startTask();
    }

    private void registerCommands() {
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.getCommandCompletions().registerCompletion("providers", c -> providerManager.getProviderNames());
        this.commandManager.registerCommand(new LeaderboardCommand(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new LeaderboardsListener(this), this);
    }

}
