package com.pandoaspen.leaderboards.providers;

import com.pandoaspen.leaderboards.LeaderboardsPlugin;
import com.pandoaspen.leaderboards.config.providers.ProviderConfig;
import com.pandoaspen.leaderboards.config.providers.TriggerConfig;
import com.pandoaspen.leaderboards.providers.dataproviders.IDataProvider;
import com.pandoaspen.leaderboards.providers.registry.PlayerData;
import com.pandoaspen.leaderboards.utils.CollectionUtils;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProviderManager {

    private final LeaderboardsPlugin plugin;
    private final Logger logger;

    @Getter private List<IDataProvider> dataProviders;
    @Getter private Map<ProviderTrigger.TriggerType, Set<ProviderTrigger>> providerTriggerMap;

    public ProviderManager(LeaderboardsPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void loadProviders() {
        this.dataProviders = new ArrayList<>();
        this.providerTriggerMap = new EnumMap<>(ProviderTrigger.TriggerType.class);

        for (ProviderConfig providerConfig : plugin.getLeaderboardConfig().getDataProviders()) {
            logger.info("Loading provider \"" + providerConfig.getName() + "\"");

            IDataProvider dataProvider = providerConfig.getType().getInstantiator().apply(plugin, providerConfig);

            for (TriggerConfig updateTrigger : providerConfig.getUpdateTriggers()) {
                ProviderTrigger.TriggerType triggerType = updateTrigger.getTriggerType();
                long delay = updateTrigger.getDelay().getMillis();

                ProviderTrigger trigger = new ProviderTrigger(plugin, dataProvider, delay);
                providerTriggerMap.computeIfAbsent(triggerType, x -> new HashSet<>()).add(trigger);
            }

            try {
                dataProvider.load();
                dataProviders.add(dataProvider);

            } catch (Exception e) {
                logger.severe("Provider \"" + providerConfig.getName() + "\" Failed to Load! Skipping");
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        saveProviders();

    }

    public void saveProviders() {
        for (IDataProvider dataProvider : dataProviders) {
            try {
                dataProvider.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public IDataProvider getProvider(String providerName) {
        if (providerName == null) return null;
        return CollectionUtils.findAny(dataProviders, p -> p.getName().equals(providerName));
    }

    public Collection<String> getProviderNames() {
        return dataProviders.stream().map(IDataProvider::getName).collect(Collectors.toList());
    }

    public void updatePlayerNames(Player player) {
        dataProviders.forEach(dp -> {
            PlayerData dataRegistry = dp.getDataFor(player.getUniqueId());
            if (dataRegistry != null) {
                dataRegistry.setPlayerName(player.getName());
            }
        });
    }

    public void triggerProviders(Player player, ProviderTrigger.TriggerType triggerType) {
        if (!providerTriggerMap.containsKey(triggerType)) return;
        providerTriggerMap.get(triggerType).forEach(trigger -> trigger.getDataProvider().collectData(player));
    }

    public void startTask() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            long time = System.currentTimeMillis();
            for (IDataProvider dataProvider : dataProviders) {
                dataProvider.runPeriodic(time);
            }
        }, 20, 20);
    }
}
