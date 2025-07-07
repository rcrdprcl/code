package sonemc.soneRPG.enums;

import org.bukkit.Material;

public enum SkillType {
    // Combat Skills
    SWORD_DAMAGE("One-Handed", "Master of swords and daggers", Material.DIAMOND_SWORD),
    BOW_DAMAGE("Archery", "Expert marksman with bows", Material.BOW),

    // Magic Skills
    RESTORATION("Restoration", "Healing and protective\n§7+1% healing from golden apples per level\n§7+2% health potion effectiveness per level\n§7+0.5% health regeneration per level\n§7+1 Alchemy level unlocked", Material.GOLDEN_APPLE),
    ENCHANTING("Enchanting", "Magical item enhancement", Material.ENCHANTING_TABLE),

    // Physical Skills
    LIGHT_ARMOR_SPEED("Light Armor", "Agility and speed enhancement", Material.LEATHER_CHESTPLATE),
    HEAVY_ARMOR("Heavy Armor", "Maximum protection and defense", Material.IRON_CHESTPLATE),
    SMITHING("Smithing", "Weapon and armor crafting", Material.ANVIL),

    // Crafting Skills
    ALCHEMY("Alchemy", "Potion brewing and poison crafting\n§7+5% potion effectiveness per level\n§7+2% poison duration per level\n§7Unlocks advanced recipes", Material.BREWING_STAND);

    private final String displayName;
    private final String description;
    private final Material icon;

    SkillType(String displayName, String description, Material icon) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public Material getIcon() { return icon; }
}