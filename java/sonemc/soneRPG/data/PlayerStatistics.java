package sonemc.soneRPG.data;

import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatistics {

    private int totalMobKills;
    private double totalDamageDealt;
    private int enchantmentsFound;
    private int highestMobLevelKilled;
    private final Map<EntityType, Integer> mobKillsByType;

    public PlayerStatistics() {
        this.totalMobKills = 0;
        this.totalDamageDealt = 0.0;
        this.enchantmentsFound = 0;
        this.highestMobLevelKilled = 0;
        this.mobKillsByType = new HashMap<>();
    }

    public void addMobKill(EntityType entityType, int mobLevel) {
        totalMobKills++;
        mobKillsByType.put(entityType, mobKillsByType.getOrDefault(entityType, 0) + 1);
        if (mobLevel > highestMobLevelKilled) {
            highestMobLevelKilled = mobLevel;
        }
    }

    public void addDamageDealt(double damage) {
        totalDamageDealt += damage;
    }

    public void addEnchantmentFound() {
        enchantmentsFound++;
    }

    // Getters and setters
    public int getTotalMobKills() {
        return totalMobKills;
    }

    public void setTotalMobKills(int totalMobKills) {
        this.totalMobKills = totalMobKills;
    }

    public double getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public void setTotalDamageDealt(double totalDamageDealt) {
        this.totalDamageDealt = totalDamageDealt;
    }

    public int getEnchantmentsFound() {
        return enchantmentsFound;
    }

    public void setEnchantmentsFound(int enchantmentsFound) {
        this.enchantmentsFound = enchantmentsFound;
    }

    public int getHighestMobLevelKilled() {
        return highestMobLevelKilled;
    }

    public void setHighestMobLevelKilled(int highestMobLevelKilled) {
        this.highestMobLevelKilled = highestMobLevelKilled;
    }

    public Map<EntityType, Integer> getMobKillsByType() {
        return mobKillsByType;
    }
}