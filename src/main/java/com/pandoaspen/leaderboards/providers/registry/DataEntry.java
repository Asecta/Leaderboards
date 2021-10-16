package com.pandoaspen.leaderboards.providers.registry;

import lombok.Data;

@Data
public class DataEntry implements Comparable<DataEntry> {

    private final long time;
    private final double value;

    @Override
    public int compareTo(DataEntry o) {
        return Long.compare(time, o.time);
    }
}
