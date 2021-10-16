package com.pandoaspen.leaderboards.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import com.pandoaspen.leaderboards.LeaderboardsPlugin;
import com.pandoaspen.leaderboards.providers.dataproviders.IDataProvider;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@CommandAlias("lb|leaderboard")
public class LeaderboardCommand extends BaseCommand {

    private final LeaderboardsPlugin plugin;

    @Subcommand("visualizer")
    @CommandCompletion("@providers")
    public void cmdVisualizer(Player sender, String providerName, int ranking) {

        IDataProvider provider = plugin.getProviderManager().getProvider(providerName);

        if (provider == null) {
            sender.sendMessage("That provider couldnt be found");
            return;
        }

        Entity entity = sender.getWorld().spawnEntity(sender.getLocation(), EntityType.ARMOR_STAND);

//        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
        //            PlayerData dataRegistry = provider.getByIndex(0, ranking - 1);
        //            entity.setCustomNameVisible(true);
        //            entity.setCustomName(dataRegistry.getPlayerName());
        //        }, 20, 20);
    }

    @Subcommand("listproviders")
    public void cmdListProviders(Player sender) {
        sender.sendMessage("Found " + plugin.getProviderManager().getDataProviders().size() + " providers: ");
        sender.sendMessage(String.join(", ", plugin.getProviderManager().getProviderNames()));
    }

    @Subcommand("top")
    @CommandCompletion("@providers")
    public void cmdTop(Player sender, String providerName, int limit) {

        IDataProvider provider = plugin.getProviderManager().getProvider(providerName);

        if (provider == null) {
            sender.sendMessage("That provider couldnt be found");
            return;
        }

        Bukkit.broadcastMessage("==============================");
//        List<PlayerData> dataRegistries = provider.getTop(0, limit);
//        for (int i = 0; i < dataRegistries.size(); i++) {
//            PlayerData dataRegistry = dataRegistries.get(i);
//            Bukkit.broadcastMessage(String.format("%d: %s - %.2f", i + 1, dataRegistry.getPlayerName(), dataRegistry.getTotal()));
//        }
    }

    @Subcommand("offline")
    public void offlineCollect(Player sender, String providerName) {
        IDataProvider provider = plugin.getProviderManager().getProvider(providerName);

        if (provider == null) {
            sender.sendMessage("That provider couldnt be found");
            return;
        }

        OfflinePlayer[] offlinePlayers = plugin.getServer().getOfflinePlayers();


        sender.sendMessage("Attempting offline collection of " + offlinePlayers.length + " players...");

        double length = offlinePlayers.length;

        AtomicInteger progress = new AtomicInteger(0);

        for (int i = 0; i < offlinePlayers.length; i++) {
            int finalI = i;
            OfflinePlayer offlinePlayer = offlinePlayers[finalI];

            try {
                provider.collectData(offlinePlayer);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            double percent = progress.incrementAndGet() / length * 100d;
            sender.sendMessage(String.format("Collecting.. %.2f%% Complete", percent));
        }

        sender.sendMessage("Done");
    }
}