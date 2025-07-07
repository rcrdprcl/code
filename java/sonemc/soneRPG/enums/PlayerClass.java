package sonemc.soneRPG.enums;

import org.bukkit.Material;

public enum PlayerClass {
    WARRIOR("Warrior", "Master of melee combat and heavy armor", Material.DIAMOND_SWORD,
            new String[]{"§7• +15% melee damage", "§7• +10% health", "§7• Reduced armor speed penalty"},
            new SkillType[]{SkillType.SWORD_DAMAGE, SkillType.HEAVY_ARMOR}),
    
    ARCHER("Archer", "Expert marksman with bow and arrow", Material.BOW,
            new String[]{"§7• +20% bow damage", "§7• +15% movement speed", "§7• Better enchantment drop rates"},
            new SkillType[]{SkillType.BOW_DAMAGE, SkillType.LIGHT_ARMOR_SPEED}),

    BERSERKER("Berserker", "Fierce warrior who grows stronger when wounded", Material.GOLDEN_AXE,
            new String[]{"§7• +30% damage when low health", "§7• Life steal on all attacks", "§7• Rage mode abilities"},
            new SkillType[]{ SkillType.SWORD_DAMAGE, SkillType.HEAVY_ARMOR});

    private final String displayName;
    private final String description;
    private final Material icon;
    private final String[] bonuses;
    private final SkillType[] preferredSkills;

    PlayerClass(String displayName, String description, Material icon, String[] bonuses, SkillType[] preferredSkills) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.bonuses = bonuses;
        this.preferredSkills = preferredSkills;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Material getIcon() {
        return icon;
    }

    public String[] getBonuses() {
        return bonuses;
    }

    public SkillType[] getPreferredSkills() {
        return preferredSkills;
    }
}