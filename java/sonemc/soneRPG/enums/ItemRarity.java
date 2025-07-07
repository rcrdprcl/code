package sonemc.soneRPG.enums;

import org.bukkit.ChatColor;

public enum ItemRarity {
    COMMON("Common", ChatColor.WHITE, 1.0, 0.0),
    UNCOMMON("Uncommon", ChatColor.GREEN, 1.1, 0.05),
    RARE("Rare", ChatColor.BLUE, 1.25, 0.15),
    EPIC("Epic", ChatColor.DARK_PURPLE, 1.5, 0.30),
    LEGENDARY("Legendary", ChatColor.GOLD, 2.0, 0.50),
    ARTIFACT("Artifact", ChatColor.RED, 3.0, 1.0);

    private final String displayName;
    private final ChatColor color;
    private final double statMultiplier;
    private final double specialChance;

    ItemRarity(String displayName, ChatColor color, double statMultiplier, double specialChance) {
        this.displayName = displayName;
        this.color = color;
        this.statMultiplier = statMultiplier;
        this.specialChance = specialChance;
    }

    public String getDisplayName() { return displayName; }
    public ChatColor getColor() { return color; }
    public double getStatMultiplier() { return statMultiplier; }
    public double getSpecialChance() { return specialChance; }
    
    public String getColoredName() { return color + displayName; }
}