package com.pandoaspen.leaderboards.providers.registry;

import com.pandoaspen.leaderboards.providers.serializers.JsonDataSerializer;
import com.pandoaspen.leaderboards.utils.CollectionUtils;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;


public class DataRegistry extends JsonDataSerializer {

    private TreeMap<UUID, PlayerData> dataRegistryMap;
    @Getter private String name;

    public DataRegistry(JavaPlugin plugin, String name) {
        super(plugin);
        this.name = name;
    }

    public void addEntry(UUID uuid, String name, double value) {
        PlayerData playerData = dataRegistryMap.computeIfAbsent(uuid, x -> new PlayerData(name));
        List<DataEntry> dataEntries = playerData.getDataEntries();

        long currentTime = System.currentTimeMillis();

        if (dataEntries.isEmpty()) {
            dataEntries.add(new DataEntry(currentTime, 0));
            return;
        }

        double last = dataEntries.get(dataEntries.size() - 1).getValue();

        if (last == value) {
            return;
        }

        dataEntries.add(new DataEntry(currentTime, value));
    }

    public double getSince(UUID uuid, long epoch) {
        if (!dataRegistryMap.containsKey(uuid)) {
            return 0;
        }

        PlayerData playerData = dataRegistryMap.get(uuid);
        List<DataEntry> dataEntries = playerData.getDataEntries();

        double scoreBefore = 0;
        double scoreTotal = 0;

        for (DataEntry dataEntry : dataEntries) {
            if (dataEntry.getTime() < epoch) {
                scoreBefore = dataEntry.getValue();
            }
            scoreTotal += dataEntry.getValue();
        }

        return scoreTotal - scoreBefore;
    }


    public List<PlayerData> getTop(long since, int limit) {
        Function<PlayerData, Double> valueFunc = d -> -d.getSince(System.currentTimeMillis() - since);
        return CollectionUtils.resolveSort(dataRegistryMap.values(), valueFunc, v -> 0 < -v, limit);
    }

    public PlayerData getByIndex(long since, int index) {
        return getTop(since, index + 1).get(index);
    }

    public PlayerData getDataFor(UUID uuid) {
        return dataRegistryMap.get(uuid);
    }

    public Map<UUID, PlayerData> getDatabase() {
        return dataRegistryMap;
    }


}
