package com.pandoaspen.leaderboards.visualizer.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.pandoaspen.leaderboards.LeaderboardsPlugin;
import com.pandoaspen.leaderboards.config.visualizers.NPCConfig;
import com.pandoaspen.leaderboards.config.visualizers.ProviderVisualizerConfig;
import com.pandoaspen.leaderboards.config.visualizers.VisualizerConfig;
import com.pandoaspen.leaderboards.providers.dataproviders.IDataProvider;
import com.pandoaspen.leaderboards.providers.registry.PlayerData;
import com.pandoaspen.leaderboards.providers.registry.PlayerScore;
import com.pandoaspen.leaderboards.visualizer.AbstractVisualizer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.ScoreboardTrait;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerVisualizer extends AbstractVisualizer {

    private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    private String npcRegistryId;
    private NPCRegistry npcRegistry;

    private Team nametagTeam;

    private long nextRunTime;

    private Hologram titleHologram;

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

        spawnHologram(providerVisualizerConfig);

        long since = providerVisualizerConfig.getDuration().getMillis();
        int max = getVisualizerConfig().getNpcs().stream().mapToInt(v -> v.getRank()).max().getAsInt() + 1;

        long start = System.nanoTime();

        IDataProvider dataProvider = getPlugin().getProviderManager().getProvider(providerVisualizerConfig.getName());

        if (dataProvider == null) {
            System.out.println(String.format("Data provider %s is not registered (%s)", providerVisualizerConfig.getName(), getPlugin().getProviderManager().getProviderNames()));
            return;
        }

        List<PlayerScore> top = dataProvider.getTop(since, max);

        double timediff = (System.nanoTime() - start) / 1000000d;
        int dpCount = dataProvider.getDatabase().values().stream().mapToInt(d -> d.getDataEntries().size()).sum();
        Bukkit.broadcastMessage(String.format("Took %.4f ms to update (%d) (%s) (%s)", timediff, dpCount, dataProvider.getName(), providerVisualizerConfig.getDuration().getDurationString()));

        String scoreFormat = dataProvider.getProviderConfig().getScoreFormat();

        for (NPCConfig npcConfig : getVisualizerConfig().getNpcs()) {
            if (npcConfig.getRank() > top.size()) continue;

            PlayerScore playerScore = top.get(npcConfig.getRank() - 1);
            String playerName = playerScore.getName();

            spawnNPC(providerVisualizerConfig, npcConfig, playerName, String.format(scoreFormat, playerScore.getValue()));
            sendTeamPacket(playerName);
        }

        ProviderVisualizerConfig nextProviderConf = providerVisualizerConfigs.get(visualizerIndex);
        long nextTime = nextProviderConf.getDuration().getMillis();
        getPlugin().getProviderManager().getProvider(nextProviderConf.getName()).prepareNextTop(nextTime, max);
    }

    private void spawnHologram(ProviderVisualizerConfig providerVisualizerConfig) {
        if (getVisualizerConfig().getTitle().isEnabled()) {
            titleHologram.clearLines();
            for (String line : providerVisualizerConfig.getTitle().split("[\r\n]")) {
                if (line.isEmpty()) continue;
                titleHologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
    }

    private void spawnNPC(ProviderVisualizerConfig providerVisualizerConfig, NPCConfig npcConfig, String playerName, String value) {
        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, playerName);

        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);

        npc.spawn(npcConfig.getLocation());

        String[] lines = providerVisualizerConfig.getNpcTitle().split("\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i];
            line = line.replaceAll("%rank%", Integer.toString(npcConfig.getRank()));
            line = line.replaceAll("%name%", playerName);
            line = line.replaceAll("%value%", value);
            line = ChatColor.translateAlternateColorCodes('&', line);
            hologramTrait.addLine(line);
        }

        LookClose lookCloseTrait = npc.getOrAddTrait(LookClose.class);
        lookCloseTrait.lookClose(true);
        lookCloseTrait.setRealisticLooking(true);
        lookCloseTrait.setRange(getVisualizerConfig().getWatchDistance());

        ScoreboardTrait scoreboardTrait = npc.getOrAddTrait(ScoreboardTrait.class);
        this.nametagTeam.addEntry(playerName);
        scoreboardTrait.apply(this.nametagTeam, false);
    }

    private void sendTeamPacket(String playerName) {
        PacketContainer packetContainer = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        packetContainer.getIntegers().write(1, 3);
        packetContainer.getStrings().write(0, "nametagHide");
        packetContainer.getSpecificModifier(Collection.class).write(0, Arrays.asList(playerName));
        protocolManager.broadcastServerPacket(packetContainer);
    }
}