package sonemc.soneRPG.enums;

import org.bukkit.Material;

public enum SkillType {
    SWORD_DAMAGE("One-Handed", "Master of swords and daggers", Material.DIAMOND_SWORD),
    BOW_DAMAGE("Archery", "Expert marksman with bows", Material.BOW),
    RESTORATION("Restoration", "Healing and protective magic", Material.GOLDEN_APPLE),
    ENCHANTING("Enchanting", "Magical item enhancement", Material.ENCHANTING_TABLE),
    LIGHT_ARMOR_SPEED("Light Armor", "Agility and speed enhancement", Material.LEATHER_CHESTPLATE),
    HEAVY_ARMOR("Heavy Armor", "Maximum protection and defense", Material.IRON_CHESTPLATE),
    SMITHING("Smithing", "Weapon and armor crafting", Material.ANVIL),
    ALCHEMY("Alchemy", "Potion brewing and poison crafting", Material.BREWING_STAND),
    STAMINA("Stamina", "Endurance and energy management\n§7+15 max stamina per level\n§7Better sprint efficiency\n§7Faster stamina regeneration", Material.FEATHER);

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