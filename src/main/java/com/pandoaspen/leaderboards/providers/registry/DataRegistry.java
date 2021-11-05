package com.pandoaspen.leaderboards.providers.registry;

import com.pandoaspen.leaderboards.providers.serializers.JsonDataSerializer;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


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

        if (value == 0) return;

        if (dataEntries.isEmpty()) {
            if (value > 5) {
                currentTime = currentTime - TimeUnit.DAYS.toMillis(364);
            }

            dataEntries.add(new DataEntry(currentTime, value));
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
        double val = getSince(data, epoch);
        if (val <= 0) return null;
        return new PlayerScore(uuid, data.getPlayerName(), epoch, val);
    }

    public List<PlayerScore> getTop(long since, int limit) {
        long epoch = System.currentTimeMillis() - since;

        PlayerScore[] result = new PlayerScore[limit];
        for (int i = 0; i < limit; i++) {
            result[i] = new PlayerScore(new UUID(0, 0), "", since, 0);
        }

        double minAccept = 0;
        boolean accepting = false;
        int count = 0;


        for (Map.Entry<UUID, PlayerData> entry : dataRegistryMap.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            PlayerScore score = getScore(entry.getKey(), entry.getValue(), epoch);
            if (score == null) continue;

            if (!accepting) {
                int index = Arrays.binarySearch(result, score);
                if (index < 0) index = ~index;
                System.arraycopy(result, index, result, index + 1, limit - index - 1);
                result[index] = score;

                if (++count == limit) {
                    minAccept = result[limit - 1].getValue();
                    accepting = true;
                }
                continue;
            }

            if (score.getValue() > minAccept) {
                int index = Arrays.binarySearch(result, score);
                if (index < 0) index = ~index;
                System.arraycopy(result, index, result, index + 1, limit - index - 1);
                result[index] = score;
                minAccept = result[limit - 1].getValue();
            }
        }


        return Arrays.asList(result);
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
