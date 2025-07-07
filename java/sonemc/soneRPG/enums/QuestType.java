package sonemc.soneRPG.enums;

public enum QuestType {
    KILL_MOBS("Kill Mobs", "Defeat a certain number of specific mobs"),
    REACH_LEVEL("Reach Level", "Achieve a specific RPG level"),
    FIND_ENCHANTMENTS("Find Enchantments", "Discover rare enchantments"),
    DEAL_DAMAGE("Deal Damage", "Deal a total amount of damage"),
    SURVIVE_TIME("Survive Time", "Survive for a certain duration"),
    COLLECT_ITEMS("Collect Items", "Gather specific items"),
    EXPLORE_BIOMES("Explore Biomes", "Visit different biomes"),
    DEFEAT_BOSS("Defeat Boss", "Defeat powerful boss mobs");

    private final String displayName;
    private final String description;

    QuestType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}