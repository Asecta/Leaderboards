package com.pandoaspen.leaderboards.providers.registry;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class PlayerData implements Iterable<DataEntry> {

    @NonNull private String playerName;
    private final List<DataEntry> dataEntries = new ArrayList<>();

    public int size() {
        return dataEntries.size();
    }

    public boolean isEmpty() {
        return dataEntries.isEmpty();
    }

    public boolean add(DataEntry dataEntry) {
        return dataEntries.add(dataEntry);
    }

    public void clear() {
        dataEntries.clear();
    }

    public DataEntry get(int index) {
        return dataEntries.get(index);
    }

    public double sum() {
        return dataEntries.stream().mapToDouble(DataEntry::getValue).sum();
    }

    @Override
    public Iterator<DataEntry> iterator() {
        return dataEntries.iterator();
    }
}
