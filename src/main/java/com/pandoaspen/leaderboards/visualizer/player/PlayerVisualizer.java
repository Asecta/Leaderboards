package com.pandoaspen.leaderboards.visualizer.player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.pandoaspen.leaderboards.LeaderboardsPlugin;
import com.pandoaspen.leaderboards.config.visualizers.NPCConfig;
import com.pandoaspen.leaderboards.config.visualizers.ProviderVisualizerConfig;
import com.pandoaspen.leaderboards.config.visualizers.VisualizerConfig;
import com.pandoaspen.leaderboards.providers.dataproviders.IDataProvider;
import com.pandoaspen.leaderboards.providers.registry.PlayerScore;
import com.pandoaspen.leaderboards.visualizer.AbstractVisualizer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.ScoreboardTrait;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerVisualizer extends AbstractVisualizer {

    private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    private String npcRegistryId;
    private NPCRegistry npcRegistry;

    private Team nametagTeam;

    private long nextRunTime;

    private Hologram titleHologram;

    List<Hologram> holograms = new ArrayList<>();

    private List<ProviderVisualizerConfig> providerVisualizerConfigs;
    int visualizerIndex = 0;

    public PlayerVisualizer(LeaderboardsPlugin plugin, VisualizerConfig visualizerConfig) {
        super(plugin, visualizerConfig);
    }

    @Override
    public void start() {
        this.providerVisualizerConfigs = getVisualizerConfig().getProviders();

        this.npcRegistryId = UUID.randomUUID().toString();
        this.npcRegistry = CitizensAPI.createNamedNPCRegistry(this.npcRegistryId, new MemoryNPCDataStore());
        this.nametagTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(UUID.randomUUID().toString().substring(0, 15));

        if (getVisualizerConfig().getTitle().isEnabled()) {
            Location location = getVisualizerConfig().getTitle().getLocation();
            titleHologram = HologramsAPI.createHologram(getPlugin(), location);
        }
    }

    @Override
    public void stop() {
        npcRegistry.deregisterAll();
        npcRegistry.despawnNPCs(DespawnReason.PLUGIN);
        titleHologram.delete();
        holograms.forEach(Hologram::delete);
        holograms.clear();
    }

    @Override
    public void update(long currentTime) {
        if (nextRunTime > currentTime) return;
        nextRunTime = currentTime + getVisualizerConfig().getRotatePeriod().getMillis();

        ProviderVisualizerConfig providerVisualizerConfig = providerVisualizerConfigs.get(visualizerIndex);
        visualizerIndex = (visualizerIndex + 1) % (providerVisualizerConfigs.size());

        npcRegistry.deregisterAll();
        npcRegistry.despawnNPCs(DespawnReason.PLUGIN);
        nametagTeam.getEntries().forEach(nametagTeam::removeEntry);

        spawnTitleHologram(providerVisualizerConfig);

        holograms.forEach(Hologram::delete);
        holograms.clear();

        long since = providerVisualizerConfig.getDuration().getMillis();
        int max = getVisualizerConfig().getNpcs().stream().mapToInt(v -> v.getRank()).max().getAsInt() + 1;

        IDataProvider dataProvider = getPlugin().getProviderManager().getProvider(providerVisualizerConfig.getName());

        if (dataProvider == null) {
            System.out.println(String.format("Data provider %s is not registered (%s)", providerVisualizerConfig.getName(), getPlugin().getProviderManager().getProviderNames()));
            return;
        }

        List<PlayerScore> top = dataProvider.getTop(since, max);

        String scoreFormat = dataProvider.getProviderConfig().getScoreFormat();

        int npcConfIndex = 0;

        for (NPCConfig npcConfig : getVisualizerConfig().getNpcs()) {
            if (npcConfig.getRank() > top.size()) continue;

            PlayerScore playerScore = top.get(npcConfig.getRank() - 1);

            String val = String.format(scoreFormat, playerScore.getValue());
            npcConfIndex++;

            int finalNpcConfIndex = npcConfIndex;
            getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {
                spawnNPCHologram(providerVisualizerConfig, npcConfig, playerScore, val, finalNpcConfIndex);
                spawnNPC(providerVisualizerConfig, npcConfig, playerScore, val);
            }, npcConfIndex * 3);


        }

        ProviderVisualizerConfig nextProviderConf = providerVisualizerConfigs.get(visualizerIndex);
        long nextTime = nextProviderConf.getDuration().getMillis();
        getPlugin().getProviderManager().getProvider(nextProviderConf.getName()).prepareNextTop(nextTime, max);
    }

    private void spawnTitleHologram(ProviderVisualizerConfig providerVisualizerConfig) {
        if (getVisualizerConfig().getTitle().isEnabled()) {
            titleHologram.clearLines();
            for (String line : providerVisualizerConfig.getTitle().split("[\r\n]")) {
                if (line.isEmpty()) continue;
                titleHologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
    }

    private void spawnNPCHologram(ProviderVisualizerConfig providerVisualizerConfig, NPCConfig npcConfig, PlayerScore playerScore, String value, int index) {

        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {
            Location hologramLocation = npcConfig.getLocation().clone();
            hologramLocation.add(0, 2.5, 0);

            Hologram hologram = HologramsAPI.createHologram(getPlugin(), hologramLocation);
            holograms.add(hologram);

            String name = playerScore.getName();

            if (name == null || name.trim().isEmpty()) {
                name = "Not Found";
            }

            String[] lines = providerVisualizerConfig.getNpcTitle().split("\n");
            for (int i = lines.length - 1; i >= 0; i--) {
                String line = lines[i];
                line = line.replaceAll("%rank%", Integer.toString(npcConfig.getRank()));
                line = line.replaceAll("%name%", name);
                line = line.replaceAll("%value%", value);
                line = ChatColor.translateAlternateColorCodes('&', line);
                hologram.appendTextLine(line);
            }
        }, 1);
    }

    private void spawnNPC(ProviderVisualizerConfig providerVisualizerConfig, NPCConfig npcConfig, PlayerScore playerScore, String value) {
        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "LB_NPC");

        npc.spawn(npcConfig.getLocation().clone());

        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinName(playerScore.getName(), false);

        LookClose lookCloseTrait = npc.getOrAddTrait(LookClose.class);
        lookCloseTrait.lookClose(true);
        lookCloseTrait.setRealisticLooking(true);
        lookCloseTrait.setRange(getVisualizerConfig().getWatchDistance());

        if (npc.getEntity() != null) {
            ScoreboardTrait scoreboardTrait = npc.getOrAddTrait(ScoreboardTrait.class);
            this.nametagTeam.addEntry("LB_NPC");
            scoreboardTrait.apply(this.nametagTeam, false);
        }

    }
}