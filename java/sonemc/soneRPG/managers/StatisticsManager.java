package sonemc.soneRPG.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerStatistics;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StatisticsManager {

    private final SoneRPG plugin;
    private final Map<UUID, PlayerStatistics> playerStats;
    private File statisticsFile;
    private FileConfiguration statisticsConfig;

    public StatisticsManager(SoneRPG plugin) {
        this.plugin = plugin;
        this.playerStats = new HashMap<>();
    }

    public void createStatisticsConfig() {
        statisticsFile = new File(plugin.getDataFolder(), "statistics.yml");
        if (!statisticsFile.exists()) {
            try {
                statisticsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create statistics.yml: " + e.getMessage());
            }
        }
        statisticsConfig = YamlConfiguration.loadConfiguration(statisticsFile);
        loadPlayerData();
    }

    public PlayerStatistics getPlayerStatistics(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId(), k -> new PlayerStatistics());
    }

    public void addMobKill(Player player, EntityType entityType, int mobLevel) {
        PlayerStatistics stats = getPlayerStatistics(player);
        stats.addMobKill(entityType, mobLevel);
        savePlayerData(player);
    }

    public void addDamageDealt(Player player, double damage) {
        PlayerStatistics stats = getPlayerStatistics(player);
        stats.addDamageDealt(damage);
        savePlayerData(player);
    }

    public void addEnchantmentFound(Player player) {
        PlayerStatistics stats = getPlayerStatistics(player);
        stats.addEnchantmentFound();
        savePlayerData(player);
    }

    public List<Map.Entry<UUID, Integer>> getTopPlayersByMobKills() {
        Map<UUID, Integer> killCounts = new HashMap<>();
        
        for (Map.Entry<UUID, PlayerStatistics> entry : playerStats.entrySet()) {
            killCounts.put(entry.getKey(), entry.getValue().getTotalMobKills());
        }
        
        List<Map.Entry<UUID, Integer>> sortedList = new ArrayList<>(killCounts.entrySet());
        sortedList.sort(Map.Entry.<UUID, Integer>comparingByValue().reversed());
        
        return sortedList.subList(0, Math.min(10, sortedList.size()));
    }

    public List<Map.Entry<UUID, Integer>> getTopPlayersByLevel() {
        Map<UUID, Integer> levels = new HashMap<>();
        
        for (UUID playerUUID : playerStats.keySet()) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player != null) {
                levels.put(playerUUID, plugin.getRPGLevelManager().getPlayerLevel(player));
            }
        }
        
        List<Map.Entry<UUID, Integer>> sortedList = new ArrayList<>(levels.entrySet());
        sortedList.sort(Map.Entry.<UUID, Integer>comparingByValue().reversed());
        
        return sortedList.subList(0, Math.min(10, sortedList.size()));
    }

    private void savePlayerData(Player player) {
        PlayerStatistics stats = playerStats.get(player.getUniqueId());
        if (stats == null) {
            return;
        }
        
        String path = "players." + player.getUniqueId().toString();
        statisticsConfig.set(path + ".total-mob-kills", stats.getTotalMobKills());
        statisticsConfig.set(path + ".total-damage-dealt", stats.getTotalDamageDealt());
        statisticsConfig.set(path + ".enchantments-found", stats.getEnchantmentsFound());
        statisticsConfig.set(path + ".highest-mob-level-killed", stats.getHighestMobLevelKilled());
        
        // Save mob kills by type
        for (Map.Entry<EntityType, Integer> entry : stats.getMobKillsByType().entrySet()) {
            statisticsConfig.set(path + ".mob-kills." + entry.getKey().name().toLowerCase(), entry.getValue());
        }
        
        saveStatisticsConfig();
    }

    private void loadPlayerData() {
        if (statisticsConfig.getConfigurationSection("players") == null) {
            return;
        }
        
        for (String uuidString : statisticsConfig.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(uuidString);
                PlayerStatistics stats = new PlayerStatistics();
                
                String path = "players." + uuidString;
                stats.setTotalMobKills(statisticsConfig.getInt(path + ".total-mob-kills", 0));
                stats.setTotalDamageDealt(statisticsConfig.getDouble(path + ".total-damage-dealt", 0.0));
                stats.setEnchantmentsFound(statisticsConfig.getInt(path + ".enchantments-found", 0));
                stats.setHighestMobLevelKilled(statisticsConfig.getInt(path + ".highest-mob-level-killed", 0));
                
                // Load mob kills by type
                if (statisticsConfig.getConfigurationSection(path + ".mob-kills") != null) {
                    for (String mobType : statisticsConfig.getConfigurationSection(path + ".mob-kills").getKeys(false)) {
                        try {
                            EntityType entityType = EntityType.valueOf(mobType.toUpperCase());
                            int kills = statisticsConfig.getInt(path + ".mob-kills." + mobType);
                            stats.getMobKillsByType().put(entityType, kills);
                        } catch (IllegalArgumentException e) {
                            // Skip invalid entity types
                        }
                    }
                }
                
                playerStats.put(playerUUID, stats);
            } catch (IllegalArgumentException e) {
                // Skip invalid UUIDs
            }
        }
    }

    public void saveAllPlayerData() {
        for (UUID playerUUID : playerStats.keySet()) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player != null) {
                savePlayerData(player);
            }
        }
    }

    private void saveStatisticsConfig() {
        try {
            statisticsConfig.save(statisticsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save statistics.yml: " + e.getMessage());
        }
    }
}