package sonemc.soneRPG.enums;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

public enum PoisonType {
    DEADLY_POISON("Deadly Poison", "§2Causes severe poison damage over time", 
                  Material.SPIDER_EYE, PotionEffectType.POISON, 200, 2),
    
    PARALYSIS_POISON("Paralysis Poison", "§8Slows and weakens enemies", 
                     Material.FERMENTED_SPIDER_EYE, PotionEffectType.SLOW, 100, 3),
    
    WEAKNESS_POISON("Weakness Poison", "§7Reduces enemy damage output", 
                    Material.POISONOUS_POTATO, PotionEffectType.WEAKNESS, 150, 1),
    
    BLINDNESS_POISON("Blindness Poison", "§0Blinds enemies temporarily", 
                     Material.INK_SAC, PotionEffectType.BLINDNESS, 80, 0),
    
    WITHER_POISON("Wither Poison", "§8Causes withering damage", 
                  Material.WITHER_ROSE, PotionEffectType.WITHER, 100, 1),
    
    CONFUSION_POISON("Confusion Poison", "§5Causes nausea and confusion", 
                     Material.PUFFERFISH, PotionEffectType.CONFUSION, 120, 0),
    
    FROST_POISON("Frost Poison", "§b Freezes enemies with cold", 
                 Material.BLUE_ICE, PotionEffectType.SLOW, 160, 2),
    
    FIRE_POISON("Fire Poison", "§6Burns enemies from within", 
                Material.BLAZE_POWDER, null, 0, 0); // Special case - sets on fire

    private final String displayName;
    private final String description;
    private final Material icon;
    private final PotionEffectType effectType;
    private final int duration;
    private final int amplifier;

    PoisonType(String displayName, String description, Material icon, 
               PotionEffectType effectType, int duration, int amplifier) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.effectType = effectType;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public Material getIcon() { return icon; }
    public PotionEffectType getEffectType() { return effectType; }
    public int getDuration() { return duration; }
    public int getAmplifier() { return amplifier; }
}