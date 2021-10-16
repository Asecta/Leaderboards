package com.pandoaspen.leaderboards.providers.dataproviders;

import com.pandoaspen.leaderboards.config.providers.ProviderConfig;
import com.pandoaspen.leaderboards.providers.registry.DataRegistry;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IDataProvider {

    String getName();

    boolean isAvailable();

    void load() throws IOException;

    void save() throws IOException;

    void collectData(OfflinePlayer player);

    List<DataRegistry> getTop(long since, int limit);

    DataRegistry getByIndex(long since, int index);

    DataRegistry getDataFor(UUID uuid);

    Map<UUID, DataRegistry> getDatabase();

    JavaPlugin getPlugin();

    boolean runPeriodic(long currentTime);

    ProviderConfig getProviderConfig();

    default void prepareNextTop(long since, int limit) {
    }


}
