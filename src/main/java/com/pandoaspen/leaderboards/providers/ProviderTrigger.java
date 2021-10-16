package com.pandoaspen.leaderboards.providers;

import com.pandoaspen.leaderboards.providers.dataproviders.IDataProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
@Getter
public class ProviderTrigger {

    private final JavaPlugin plugin;
    private final IDataProvider dataProvider;
    private final long delay;

    public void run(OfflinePlayer player) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            dataProvider.collectData(player);
        }, delay / 50);
    }

    public enum TriggerType {
        PLAYER_JOIN, PLAYER_QUIT, PLAYER_KILL, PLAYER_DEATH, PLAYER_KILL_MOB
    }
}