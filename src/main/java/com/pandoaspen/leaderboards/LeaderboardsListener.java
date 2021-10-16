package com.pandoaspen.leaderboards;

import com.pandoaspen.leaderboards.providers.ProviderTrigger;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class LeaderboardsListener implements Listener {

    private final LeaderboardsPlugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getProviderManager().updatePlayerNames(event.getPlayer());
        plugin.getProviderManager().triggerProviders(event.getPlayer(), ProviderTrigger.TriggerType.PLAYER_JOIN);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getProviderManager().triggerProviders(event.getPlayer(), ProviderTrigger.TriggerType.PLAYER_QUIT);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getProviderManager().triggerProviders(event.getEntity(), ProviderTrigger.TriggerType.PLAYER_DEATH);

        System.out.println("getting killer");

        if (event.getEntity().getKiller() != null) {
            System.out.println("doing killer thing");
            plugin.getProviderManager().triggerProviders(event.getEntity().getKiller(), ProviderTrigger.TriggerType.PLAYER_KILL);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;
        if (event.getEntity().getKiller() != null) {
            plugin.getProviderManager().triggerProviders(event.getEntity().getKiller(), ProviderTrigger.TriggerType.PLAYER_KILL_MOB);
        }
    }
}
