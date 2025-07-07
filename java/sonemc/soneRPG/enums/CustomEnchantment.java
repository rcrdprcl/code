package sonemc.soneRPG.enums;

import org.bukkit.Material;

public enum CustomEnchantment {
    // Weapon Enchantments
    RAGE("Rage", "§c+15% chance to instantly kill enemies under 30% HP", 
         new Material[]{Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD,
                       Material.DIAMOND_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.STONE_AXE, Material.WOODEN_AXE,
                       Material.NETHERITE_SWORD, Material.NETHERITE_AXE}, 
         120, Material.DIAMOND_SWORD),
    
    SHARP_EYE("Sharp Eye", "§a+8% chance to instantly kill enemies under 25% HP", 
              new Material[]{Material.BOW, Material.CROSSBOW}, 
              150, Material.BOW),
    
    GOLDEN_FIST("Golden Fist", "§6+12% total damage", 
                new Material[]{Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD,
                              Material.DIAMOND_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.STONE_AXE, Material.WOODEN_AXE,
                              Material.NETHERITE_SWORD, Material.NETHERITE_AXE}, 
                100, Material.GOLDEN_SWORD),
    
    FROST_BITE("Frost Bite", "§b+25% chance to slow and freeze enemies for 5 seconds", 
               new Material[]{Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD,
                             Material.DIAMOND_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.STONE_AXE, Material.WOODEN_AXE,
                             Material.NETHERITE_SWORD, Material.NETHERITE_AXE}, 
               140, Material.ICE),
    
    BERSERKER("Berserker", "§4+3% chance of damaging 25% of mob's health when its health is below 50%", 
              new Material[]{Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD,
                            Material.DIAMOND_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.STONE_AXE, Material.WOODEN_AXE,
                            Material.NETHERITE_SWORD, Material.NETHERITE_AXE}, 
              160, Material.REDSTONE),

    FIRE_DAMAGE("Fire Damage", "§6+3% chance to set enemies ablaze for 3 seconds", 
                new Material[]{Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD,
                              Material.DIAMOND_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.STONE_AXE, Material.WOODEN_AXE,
                              Material.NETHERITE_SWORD, Material.NETHERITE_AXE}, 
                110, Material.BLAZE_POWDER),

    // Armor Enchantments
    FORTIFY_HEALTH("Fortify Health", "§c+20% maximum health", 
                   new Material[]{Material.DIAMOND_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, 
                                 Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.NETHERITE_CHESTPLATE}, 
                   250, Material.GOLDEN_APPLE),

    FORTIFY_SPEED("Fortify Speed", "§b+25% movement speed", 
                  new Material[]{Material.DIAMOND_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, 
                                Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.NETHERITE_BOOTS}, 
                  180, Material.SUGAR),

    FIRE_RESISTANCE("Fire Resistance", "§6+50% resistance to fire damage", 
                    new Material[]{Material.DIAMOND_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, 
                                  Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.NETHERITE_CHESTPLATE}, 
                    200, Material.MAGMA_CREAM),

    FROST_RESISTANCE("Frost Resistance", "§b+50% resistance to frost damage", 
                     new Material[]{Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, 
                                   Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.NETHERITE_LEGGINGS}, 
                     200, Material.PACKED_ICE),

    WATERBREATHING("Waterbreathing", "§9Unlimited underwater breathing", 
                   new Material[]{Material.DIAMOND_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET, 
                                 Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.NETHERITE_HELMET}, 
                   400, Material.PRISMARINE_SHARD),

    REGENERATION("Regeneration", "§a+2 health regeneration every 5 seconds", 
                 new Material[]{Material.DIAMOND_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, 
                               Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.NETHERITE_CHESTPLATE}, 
                 280, Material.GLISTERING_MELON_SLICE);
    
    private final String displayName;
    private final String description;
    private final Material[] applicableItems;
    private final int dropChance; // 1 in X chance
    private final Material icon;
    
    CustomEnchantment(String displayName, String description, Material[] applicableItems, int dropChance, Material icon) {
        this.displayName = displayName;
        this.description = description;
        this.applicableItems = applicableItems;
        this.dropChance = dropChance;
        this.icon = icon;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Material[] getApplicableItems() {
        return applicableItems;
    }
    
    public int getDropChance() {
        return dropChance;
    }
    
    public Material getIcon() {
        return icon;
    }
    
    public boolean canApplyTo(Material material) {
        for (Material applicable : applicableItems) {
            if (applicable == material) {
                return true;
            }
        }
        return false;
    }

    public boolean isArmorEnchantment() {
        String name = this.name();
        return name.equals("FORTIFY_HEALTH") || name.equals("FORTIFY_SPEED") || 
               name.equals("FIRE_RESISTANCE") || name.equals("FROST_RESISTANCE") || 
               name.equals("WATERBREATHING") || name.equals("REGENERATION");
    }
}