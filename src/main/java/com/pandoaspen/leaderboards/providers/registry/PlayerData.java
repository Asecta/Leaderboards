package com.pandoaspen.leaderboards.providers.registry;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class PlayerData implements Iterable<DataEntry> {

    private String playerName;

    private final List<DataEntry> dataEntries;

    public PlayerData(String playerName) {
        this.playerName = playerName;
        this.dataEntries = new ArrayList<>();
    }

    public void register(double value) {
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

    public double getTotal() {
        return dataEntries.get(dataEntries.size() - 1).getValue();
    }

    public double getSince(long epoch) {
        double scoreBefore = 0;

        for (DataEntry dataEntry : dataEntries) {
            if (dataEntry.getTime() > epoch) {
                break;
            }
            scoreBefore = dataEntry.getValue();
        }

        return getTotal() - scoreBefore;
    }

    @Override
    public Iterator<DataEntry> iterator() {
        return dataEntries.iterator();
    }
}
