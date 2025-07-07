package sonemc.soneRPG.enums;

import org.bukkit.Material;

public enum PlayerRace {
    NORD("Nord", "People of the north and born warriors", Material.IRON_SWORD,
          new String[]{"§7• +2% coins per kill", "§7• +15% cold resistance", "§7• +6% health regeneration", "§7• +3% XP gain if mob killed in ≤ 3 hits"},
          new SkillType[]{SkillType.SWORD_DAMAGE, SkillType.HEAVY_ARMOR});

    private final String displayName;
    private final String description;
    private final Material icon;
    private final String[] racialAbilities;
    private final SkillType[] preferredSkills;

    PlayerRace(String displayName, String description, Material icon, String[] racialAbilities, SkillType[] preferredSkills) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.racialAbilities = racialAbilities;
        this.preferredSkills = preferredSkills;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public Material getIcon() { return icon; }
    public String[] getRacialAbilities() { return racialAbilities; }
    public SkillType[] getPreferredSkills() { return preferredSkills; }
}