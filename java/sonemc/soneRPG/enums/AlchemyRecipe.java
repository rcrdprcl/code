package sonemc.soneRPG.enums;

import org.bukkit.Material;

public enum AlchemyRecipe {
    // Healing Potions
    MINOR_HEALING("Minor Healing Potion", "§aRestores 4 hearts", Material.POTION,
                  new Material[]{Material.GLISTERING_MELON_SLICE, Material.GLASS_BOTTLE},
                  new int[]{1, 1}, 1, 50),
    
    HEALING("Healing Potion", "§aRestores 8 hearts", Material.POTION,
            new Material[]{Material.GLISTERING_MELON_SLICE, Material.GOLDEN_CARROT, Material.GLASS_BOTTLE},
            new int[]{2, 1, 1}, 3, 120),
    
    GREATER_HEALING("Greater Healing Potion", "§aRestores full health", Material.POTION,
                    new Material[]{Material.ENCHANTED_GOLDEN_APPLE, Material.GLISTERING_MELON_SLICE, Material.GLASS_BOTTLE},
                    new int[]{1, 3, 1}, 6, 300),

    // Poisons
    DEADLY_POISON_RECIPE("Deadly Poison", "§2Poison for weapon coating", Material.SPIDER_EYE,
                         new Material[]{Material.SPIDER_EYE, Material.FERMENTED_SPIDER_EYE, Material.GUNPOWDER},
                         new int[]{2, 1, 1}, 4, 150),
    
    PARALYSIS_POISON_RECIPE("Paralysis Poison", "§8Paralysis for weapon coating", Material.FERMENTED_SPIDER_EYE,
                            new Material[]{Material.FERMENTED_SPIDER_EYE, Material.SLIME_BALL, Material.COBWEB},
                            new int[]{1, 2, 1}, 5, 180),
    
    WEAKNESS_POISON_RECIPE("Weakness Poison", "§7Weakness for weapon coating", Material.POISONOUS_POTATO,
                           new Material[]{Material.POISONOUS_POTATO, Material.ROTTEN_FLESH, Material.BONE_MEAL},
                           new int[]{2, 1, 1}, 3, 100),
    
    FROST_POISON_RECIPE("Frost Poison", "§bFrost for weapon coating", Material.BLUE_ICE,
                        new Material[]{Material.BLUE_ICE, Material.SNOWBALL, Material.PRISMARINE_CRYSTALS},
                        new int[]{1, 3, 1}, 6, 220),

    // Enhancement Potions
    STRENGTH_POTION("Strength Potion", "§c+25% damage for 5 minutes", Material.POTION,
                    new Material[]{Material.BLAZE_POWDER, Material.NETHER_WART, Material.GLASS_BOTTLE},
                    new int[]{1, 2, 1}, 4, 180),
    
    SPEED_POTION("Speed Potion", "§b+30% speed for 5 minutes", Material.POTION,
               new Material[]{Material.SUGAR, Material.NETHER_WART, Material.RABBIT_FOOT, Material.GLASS_BOTTLE},
               new int[]{2, 1, 1, 1}, 3, 150),
    
    NIGHT_VISION("Night Vision Potion", "§eSee in the dark for 8 minutes", Material.POTION,
                 new Material[]{Material.GOLDEN_CARROT, Material.NETHER_WART, Material.GLASS_BOTTLE},
                 new int[]{1, 1, 1}, 2, 100),
    
    FIRE_RESISTANCE("Fire Resistance Potion", "§6Immune to fire for 8 minutes", Material.POTION,
                    new Material[]{Material.MAGMA_CREAM, Material.NETHER_WART, Material.GLASS_BOTTLE},
                    new int[]{1, 1, 1}, 3, 140),

    // Special Potions
    INVISIBILITY("Invisibility Potion", "§8Become invisible for 3 minutes", Material.POTION,
                 new Material[]{Material.FERMENTED_SPIDER_EYE, Material.GOLDEN_CARROT, Material.PHANTOM_MEMBRANE, Material.GLASS_BOTTLE},
                 new int[]{1, 1, 1, 1}, 7, 400),
    
    WATER_BREATHING("Water Breathing Potion", "§9Breathe underwater for 8 minutes", Material.POTION,
                    new Material[]{Material.PUFFERFISH, Material.NETHER_WART, Material.GLASS_BOTTLE},
                    new int[]{1, 1, 1}, 3, 120),
    
    LEVITATION("Levitation Potion", "§dFloat in the air for 30 seconds", Material.POTION,
               new Material[]{Material.SHULKER_SHELL, Material.PHANTOM_MEMBRANE, Material.GLASS_BOTTLE},
               new int[]{1, 1, 1}, 8, 500),

    // Crafting Materials
    ALCHEMICAL_SOLVENT("Alchemical Solvent", "§7Base ingredient for advanced potions", Material.HONEY_BOTTLE,
                       new Material[]{Material.WATER_BUCKET, Material.NETHER_WART, Material.REDSTONE},
                       new int[]{1, 3, 2}, 2, 60),
    
    CONCENTRATED_ESSENCE("Concentrated Essence", "§5Powerful alchemical component", Material.DRAGON_BREATH,
                         new Material[]{Material.ENDER_PEARL, Material.GHAST_TEAR, Material.BLAZE_POWDER},
                         new int[]{1, 1, 2}, 9, 800);

    private final String name;
    private final String description;
    private final Material result;
    private final Material[] ingredients;
    private final int[] amounts;
    private final int requiredLevel;
    private final int xpGain;

    AlchemyRecipe(String name, String description, Material result, 
                  Material[] ingredients, int[] amounts, int requiredLevel, int xpGain) {
        this.name = name;
        this.description = description;
        this.result = result;
        this.ingredients = ingredients;
        this.amounts = amounts;
        this.requiredLevel = requiredLevel;
        this.xpGain = xpGain;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Material getResult() { return result; }
    public Material[] getIngredients() { return ingredients; }
    public int[] getAmounts() { return amounts; }
    public int getRequiredLevel() { return requiredLevel; }
    public int getXpGain() { return xpGain; }
}