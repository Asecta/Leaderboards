package com.pandoaspen.leaderboards.providers.registry;

import lombok.Value;

import java.util.UUID;

@Value
public class PlayerScore implements Comparable<PlayerScore> {
    private UUID uuid;
    private String name;
    private long since;
    private double value;

    @Override
    public int compareTo(PlayerScore o) {
        return Double.compare(o.value, value);
    }
}