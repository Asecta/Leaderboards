package com.pandoaspen.leaderboards.providers.dataproviders;

import com.pandoaspen.leaderboards.config.providers.ProviderConfig;
import com.pandoaspen.leaderboards.providers.registry.DataRegistry;
import com.pandoaspen.leaderboards.utils.CollectionUtils;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Getter
public class PapiDataProvider extends AbstractProvider {

    private final String name;
    private final String placeholder;

    private long nextRunTime = 0;

    private boolean available = false;

    private TreeMap<UUID, DataRegistry> dataRegistryMap;

    public PapiDataProvider(JavaPlugin plugin, ProviderConfig providerConfig) {
        super(plugin, providerConfig);
        this.name = providerConfig.getName();
        this.placeholder = providerConfig.getPlaceholder();
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
        this.dataRegistryMap = new TreeMap<>(super.readData());
    }

    @Override
    public void save() throws IOException {
        super.writeData(dataRegistryMap);
    }

    @Override
    public void collectData(OfflinePlayer player) {
        double data = parsePlaceholder(player);
        UUID uuid = player.getUniqueId();
        String lastName = player.getName();
        DataRegistry playerData = dataRegistryMap.computeIfAbsent(uuid, x -> new DataRegistry(lastName));
        playerData.register(data);
    }


    @Override
    public List<DataRegistry> getTop(long since, int limit) {
        return CollectionUtils.resolveSort(dataRegistryMap.values(), d -> -d.getSince(System.currentTimeMillis() - since), v -> 0 < -v, limit);
    }

    @Override
    public DataRegistry getByIndex(long since, int index) {
        return getTop(since, index + 1).get(index);
    }

    @Override
    public DataRegistry getDataFor(UUID uuid) {
        return dataRegistryMap.get(uuid);
    }

    @Override
    public Map<UUID, DataRegistry> getDatabase() {
        return dataRegistryMap;
    }
}
