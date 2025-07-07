package sonemc.soneRPG.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import sonemc.soneRPG.SoneRPG;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    
    private final SoneRPG plugin;
    private File mobsFile;
    private FileConfiguration mobsConfig;
    
    public ConfigManager(SoneRPG plugin) {
        this.plugin = plugin;
    }
    
    public void createMobsConfig() {
        mobsFile = new File(plugin.getDataFolder(), "mobs.yml");
        if (!mobsFile.exists()) {
            plugin.saveResource("mobs.yml", false);
        }
        mobsConfig = YamlConfiguration.loadConfiguration(mobsFile);
    }
    
    public FileConfiguration getMobsConfig() {
        return mobsConfig;
    }
    
    public void saveMobsConfig() {
        try {
            mobsConfig.save(mobsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save mobs.yml: " + e.getMessage());
        }
    }
    
    public boolean isMobEnabled(EntityType entityType) {
        String path = "enabled-mobs." + entityType.name().toLowerCase();
        return mobsConfig.getBoolean(path, false);
    }
    
    public int getMobBaseXP(EntityType entityType) {
        String path = "mob-xp." + entityType.name().toLowerCase();
        return mobsConfig.getInt(path, 10);
    }
}