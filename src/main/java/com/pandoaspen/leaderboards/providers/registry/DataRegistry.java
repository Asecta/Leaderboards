package com.pandoaspen.leaderboards.providers.registry;

import com.pandoaspen.leaderboards.providers.serializers.JsonDataSerializer;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class DataRegistry extends JsonDataSerializer {

    private TreeMap<UUID, PlayerData> dataRegistryMap;
    @Getter private String name;

    public DataRegistry(JavaPlugin plugin, String name) {
        super(plugin);
        this.name = name;
    }


    public void load() throws IOException {
        this.dataRegistryMap = new TreeMap<>(super.readData());
    }

    public void save() throws IOException {
        super.writeData(dataRegistryMap);
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

    private double getSince(PlayerData playerData, long epoch) {
        double scoreBefore = 0;

        for (DataEntry dataEntry : playerData) {
            if (dataEntry.getTime() > epoch) {
                break;
            }
            scoreBefore = dataEntry.getValue();
        }

        return playerData.get(playerData.size() - 1).getValue() - scoreBefore;
    }

    private PlayerScore getScore(UUID uuid, PlayerData data, long epoch) {
        return new PlayerScore(uuid, data.getPlayerName(), epoch, getSince(data, epoch));
    }

    public List<PlayerScore> getTop(long since, int limit) {
        long epoch = System.currentTimeMillis() - since;
        System.out.println(new Date(epoch));
        return dataRegistryMap.entrySet().parallelStream().filter(entry -> !entry.getValue().isEmpty()).map(entry -> getScore(entry.getKey(), entry.getValue(), epoch)).sorted().limit(limit).collect(Collectors.toList());

        //        Function<PlayerData, Double> valueFunc = d -> -getSince(d, System.currentTimeMillis() - since);
        //        return CollectionUtils.resolveSort(dataRegistryMap.values(), valueFunc, v -> 0 < -v, limit);
    }

    public PlayerScore getByIndex(long since, int index) {
        return getTop(since, index + 1).get(index);
    }

    public PlayerData getDataFor(UUID uuid) {
        return dataRegistryMap.get(uuid);
    }

    public Map<UUID, PlayerData> getDatabase() {
        return dataRegistryMap;
    }


}
