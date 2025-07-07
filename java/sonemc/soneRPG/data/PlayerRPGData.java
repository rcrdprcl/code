package sonemc.soneRPG.data;

import sonemc.soneRPG.enums.PlayerClass;
import sonemc.soneRPG.enums.PlayerRace;

import java.util.HashMap;
import java.util.Map;

public class PlayerRPGData {
    private PlayerClass playerClass;
    private PlayerRace playerRace;
    private int coins;
    private final Map<String, Quest> activeQuests;
    private final Map<String, Quest> completedQuests;
    private boolean hasChosenClass;
    private boolean hasChosenRace;
    private int prestigeLevel;
    private long totalPlayTime;
    private int killStreak;
    private int highestKillStreak;
    private String guildName;
    private int guildRank;
    private final Map<String, Integer> reputation;
    private final Map<String, Long> cooldowns;
    private boolean hasHouse;
    private String houseLocation;
    private double maxHealth;
    private double healthRegenRate;

    public PlayerRPGData() {
        this.playerClass = null;
        this.playerRace = null;
        this.coins = 100; // Starting money
        this.activeQuests = new HashMap<>();
        this.completedQuests = new HashMap<>();
        this.hasChosenClass = false;
        this.hasChosenRace = false;
        this.prestigeLevel = 0;
        this.totalPlayTime = 0;
        this.killStreak = 0;
        this.highestKillStreak = 0;
        this.guildName = null;
        this.guildRank = 0;
        this.reputation = new HashMap<>();
        this.cooldowns = new HashMap<>();
        this.hasHouse = false;
        this.houseLocation = null;
        this.maxHealth = 20.0; // Default Minecraft health
        this.healthRegenRate = 1.0; // Default regen rate
    }

    public void regenerateResources() {
        long currentTime = System.currentTimeMillis();
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public boolean spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

    public void incrementKillStreak() {
        killStreak++;
        if (killStreak > highestKillStreak) {
            highestKillStreak = killStreak;
        }
    }

    public void resetKillStreak() {
        killStreak = 0;
    }

    public void addReputation(String faction, int amount) {
        reputation.put(faction, reputation.getOrDefault(faction, 0) + amount);
    }

    public int getReputation(String faction) {
        return reputation.getOrDefault(faction, 0);
    }

    public void setCooldown(String ability, long duration) {
        cooldowns.put(ability, System.currentTimeMillis() + duration);
    }

    public boolean isOnCooldown(String ability) {
        Long cooldownTime = cooldowns.get(ability);
        if (cooldownTime == null) return false;
        return System.currentTimeMillis() < cooldownTime;
    }

    public long getCooldownRemaining(String ability) {
        Long cooldownTime = cooldowns.get(ability);
        if (cooldownTime == null) return 0;
        long remaining = cooldownTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    // Getters and setters
    public PlayerClass getPlayerClass() { return playerClass; }
    public void setPlayerClass(PlayerClass playerClass) { this.playerClass = playerClass; }
    
    public PlayerRace getPlayerRace() { return playerRace; }
    public void setPlayerRace(PlayerRace playerRace) { this.playerRace = playerRace; }
    
    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
    
    public Map<String, Quest> getActiveQuests() { return activeQuests; }
    public Map<String, Quest> getCompletedQuests() { return completedQuests; }
    
    public boolean hasChosenClass() { return hasChosenClass; }
    public void setHasChosenClass(boolean hasChosenClass) { this.hasChosenClass = hasChosenClass; }
    
    public boolean hasChosenRace() { return hasChosenRace; }
    public void setHasChosenRace(boolean hasChosenRace) { this.hasChosenRace = hasChosenRace; }
    
    public int getPrestigeLevel() { return prestigeLevel; }
    public void setPrestigeLevel(int prestigeLevel) { this.prestigeLevel = prestigeLevel; }
    
    public long getTotalPlayTime() { return totalPlayTime; }
    public void setTotalPlayTime(long totalPlayTime) { this.totalPlayTime = totalPlayTime; }
    
    public int getKillStreak() { return killStreak; }
    public void setKillStreak(int killStreak) { this.killStreak = killStreak; }
    
    public int getHighestKillStreak() { return highestKillStreak; }
    public void setHighestKillStreak(int highestKillStreak) { this.highestKillStreak = highestKillStreak; }
    
    public String getGuildName() { return guildName; }
    public void setGuildName(String guildName) { this.guildName = guildName; }
    
    public int getGuildRank() { return guildRank; }
    public void setGuildRank(int guildRank) { this.guildRank = guildRank; }
    
    public Map<String, Integer> getReputation() { return reputation; }
    public Map<String, Long> getCooldowns() { return cooldowns; }
    
    public boolean hasHouse() { return hasHouse; }
    public void setHasHouse(boolean hasHouse) { this.hasHouse = hasHouse; }
    
    public String getHouseLocation() { return houseLocation; }
    public void setHouseLocation(String houseLocation) { this.houseLocation = houseLocation; }

    public double getMaxHealth() { return maxHealth; }
    public void setMaxHealth(double maxHealth) { this.maxHealth = maxHealth; }

    public double getHealthRegenRate() { return healthRegenRate; }
    public void setHealthRegenRate(double healthRegenRate) { this.healthRegenRate = healthRegenRate; }
}