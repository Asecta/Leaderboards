package com.pandoaspen.leaderboards.providers.dataproviders;

import com.pandoaspen.leaderboards.config.providers.ProviderConfig;
import com.pandoaspen.leaderboards.providers.registry.DataRegistry;
import com.pandoaspen.leaderboards.providers.registry.PlayerData;
import com.pandoaspen.leaderboards.providers.registry.PlayerScore;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class PapiDataProvider extends AbstractProvider {

    private final String name;
    private final String placeholder;

    private long nextRunTime = 0;

    private boolean available = false;

    private DataRegistry dataRegistry;

    public PapiDataProvider(JavaPlugin plugin, ProviderConfig providerConfig) {
        super(plugin, providerConfig);
        this.name = providerConfig.getName();
        this.placeholder = providerConfig.getPlaceholder();
        this.dataRegistry = new DataRegistry(plugin, getName());
    }

    public double parsePlaceholder(OfflinePlayer player) {
        String placeholder = getPlaceholder();
        double data = 0;
        try {
            data = Double.parseDouble(PlaceholderAPI.setPlaceholders(player, placeholder));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public boolean runPeriodic(long currentTime) {
        if (nextRunTime < currentTime) {
            getPlugin().getLogger().fine("Running data collection for " + name);
            getPlugin().getServer().getOnlinePlayers().forEach(this::collectData);
            nextRunTime = currentTime + getProviderConfig().getUpdatePeriod().getMillis();
            return true;
        }
        return false;
    }

    @Override
    public void load() throws IOException {
        this.dataRegistry.load();
    }

    @Override
    public void save() throws IOException {
        this.dataRegistry.save();
    }

    @Override
    public void collectData(OfflinePlayer player) {
        double data = parsePlaceholder(player);
        UUID uuid = player.getUniqueId();
        String lastName = player.getName();
        dataRegistry.addEntry(uuid, lastName, data);
    }


    @Override
    public List<PlayerScore> getTop(long since, int limit) {
        return dataRegistry.getTop(since, limit);
    }

    @Override
    public PlayerScore getByIndex(long since, int index) {
        return dataRegistry.getByIndex(since, index);
    }

    @Override
    public PlayerData getDataFor(UUID uuid) {
        return dataRegistry.getDataFor(uuid);
    }

    @Override
    public Map<UUID, PlayerData> getDatabase() {
        return dataRegistry.getDatabase();
    }
}
