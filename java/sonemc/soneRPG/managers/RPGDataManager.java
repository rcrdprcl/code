package sonemc.soneRPG.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerRPGData;
import sonemc.soneRPG.enums.PlayerClass;
import sonemc.soneRPG.enums.PlayerRace;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RPGDataManager {

    private final SoneRPG plugin;
    private final Map<UUID, PlayerRPGData> playerData;
    private File rpgDataFile;
    private FileConfiguration rpgDataConfig;

    public RPGDataManager(SoneRPG plugin) {
        this.plugin = plugin;
        this.playerData = new HashMap<>();
    }

    public void createRPGDataConfig() {
        rpgDataFile = new File(plugin.getDataFolder(), "rpgdata.yml");
        if (!rpgDataFile.exists()) {
            try {
                rpgDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create rpgdata.yml: " + e.getMessage());
            }
        }
        rpgDataConfig = YamlConfiguration.loadConfiguration(rpgDataFile);
        loadPlayerData();
    }

    public PlayerRPGData getPlayerRPGData(Player player) {
        PlayerRPGData data = playerData.computeIfAbsent(player.getUniqueId(), k -> new PlayerRPGData());
        data.regenerateResources();
        return data;
    }

    public void savePlayerData(Player player) {
        PlayerRPGData data = playerData.get(player.getUniqueId());
        if (data == null) return;

        String path = "players." + player.getUniqueId().toString();

        if (data.getPlayerClass() != null) {
            rpgDataConfig.set(path + ".class", data.getPlayerClass().name());
        }
        if (data.getPlayerRace() != null) {
            rpgDataConfig.set(path + ".race", data.getPlayerRace().name());
        }
        rpgDataConfig.set(path + ".coins", data.getCoins());
        rpgDataConfig.set(path + ".has-chosen-class", data.hasChosenClass());
        rpgDataConfig.set(path + ".has-chosen-race", data.hasChosenRace());
        rpgDataConfig.set(path + ".prestige-level", data.getPrestigeLevel());
        rpgDataConfig.set(path + ".total-play-time", data.getTotalPlayTime());
        rpgDataConfig.set(path + ".kill-streak", data.getKillStreak());
        rpgDataConfig.set(path + ".highest-kill-streak", data.getHighestKillStreak());
        rpgDataConfig.set(path + ".max-health", data.getMaxHealth());
        rpgDataConfig.set(path + ".health-regen-rate", data.getHealthRegenRate());

        saveRPGDataConfig();
    }

    private void loadPlayerData() {
        if (rpgDataConfig.getConfigurationSection("players") == null) {
            return;
        }

        for (String uuidString : rpgDataConfig.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(uuidString);
                PlayerRPGData data = new PlayerRPGData();

                String path = "players." + uuidString;

                String className = rpgDataConfig.getString(path + ".class");
                if (className != null) {
                    try {
                        data.setPlayerClass(PlayerClass.valueOf(className));
                    } catch (IllegalArgumentException e) {
                        // Invalid class name, skip
                    }
                }

                String raceName = rpgDataConfig.getString(path + ".race");
                if (raceName != null) {
                    try {
                        data.setPlayerRace(PlayerRace.valueOf(raceName));
                    } catch (IllegalArgumentException e) {
                        // Invalid race name, skip
                    }
                }

                data.setCoins(rpgDataConfig.getInt(path + ".coins", 100));
                data.setHasChosenClass(rpgDataConfig.getBoolean(path + ".has-chosen-class", false));
                data.setHasChosenRace(rpgDataConfig.getBoolean(path + ".has-chosen-race", false));
                data.setPrestigeLevel(rpgDataConfig.getInt(path + ".prestige-level", 0));
                data.setTotalPlayTime(rpgDataConfig.getLong(path + ".total-play-time", 0));
                data.setKillStreak(rpgDataConfig.getInt(path + ".kill-streak", 0));
                data.setHighestKillStreak(rpgDataConfig.getInt(path + ".highest-kill-streak", 0));
                data.setMaxHealth(rpgDataConfig.getDouble(path + ".max-health", 20.0));
                data.setHealthRegenRate(rpgDataConfig.getDouble(path + ".health-regen-rate", 1.0));

                playerData.put(playerUUID, data);
            } catch (IllegalArgumentException e) {
                // Skip invalid UUIDs
            }
        }
    }

    public void saveAllPlayerData() {
        for (UUID playerUUID : playerData.keySet()) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player != null) {
                savePlayerData(player);
            }
        }
    }

    private void saveRPGDataConfig() {
        try {
            rpgDataConfig.save(rpgDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save rpgdata.yml: " + e.getMessage());
        }
    }
}