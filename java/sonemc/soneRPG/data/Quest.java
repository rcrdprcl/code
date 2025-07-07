package sonemc.soneRPG.data;

import sonemc.soneRPG.enums.QuestType;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public class Quest {
    private final String id;
    private final String name;
    private final String description;
    private final QuestType type;
    private final int targetAmount;
    private final String targetData; // For specific mob types, items, etc.
    private final List<QuestReward> rewards;
    private final int requiredLevel;
    private final Material icon;
    
    private int progress;
    private boolean completed;
    private boolean claimed;

    public Quest(String id, String name, String description, QuestType type, int targetAmount, 
                String targetData, List<QuestReward> rewards, int requiredLevel, Material icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.targetAmount = targetAmount;
        this.targetData = targetData;
        this.rewards = rewards;
        this.requiredLevel = requiredLevel;
        this.icon = icon;
        this.progress = 0;
        this.completed = false;
        this.claimed = false;
    }

    public void addProgress(int amount) {
        if (!completed) {
            progress = Math.min(progress + amount, targetAmount);
            if (progress >= targetAmount) {
                completed = true;
            }
        }
    }

    public double getProgressPercentage() {
        return (double) progress / targetAmount * 100.0;
    }

    public boolean isAvailable(int playerLevel) {
        return playerLevel >= requiredLevel;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public QuestType getType() { return type; }
    public int getTargetAmount() { return targetAmount; }
    public String getTargetData() { return targetData; }
    public List<QuestReward> getRewards() { return rewards; }
    public int getRequiredLevel() { return requiredLevel; }
    public Material getIcon() { return icon; }
    public int getProgress() { return progress; }
    public boolean isCompleted() { return completed; }
    public boolean isClaimed() { return claimed; }
    
    public void setProgress(int progress) { this.progress = progress; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setClaimed(boolean claimed) { this.claimed = claimed; }

    public static class QuestReward {
        private final RewardType type;
        private final int amount;
        private final String data;

        public QuestReward(RewardType type, int amount, String data) {
            this.type = type;
            this.amount = amount;
            this.data = data;
        }

        public RewardType getType() { return type; }
        public int getAmount() { return amount; }
        public String getData() { return data; }

        public enum RewardType {
            XP, SKILL_POINTS, ENCHANTMENT, ITEM, COINS
        }
    }
}