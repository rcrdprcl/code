package sonemc.soneRPG.managers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.AlchemyRecipe;
import sonemc.soneRPG.enums.PoisonType;
import sonemc.soneRPG.enums.SkillType;

import java.util.ArrayList;
import java.util.List;

public class AlchemyManager {
    
    private final SoneRPG plugin;
    
    public AlchemyManager(SoneRPG plugin) {
        this.plugin = plugin;
    }
    
    public boolean craftPotion(Player player, AlchemyRecipe recipe) {
        int alchemyLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.ALCHEMY);
        
        if (alchemyLevel < recipe.getRequiredLevel()) {
            player.sendMessage("§cYou need Alchemy level " + recipe.getRequiredLevel() + " to craft this!");
            return false;
        }
        
        if (!hasIngredients(player, recipe)) {
            player.sendMessage("§cYou don't have the required ingredients!");
            return false;
        }
        
        // Consume ingredients
        consumeIngredients(player, recipe);
        
        // Create result item
        ItemStack result = createPotionItem(recipe);
        player.getInventory().addItem(result);
        
        // Award XP
        plugin.getSkillManager().addSkillXP(player, SkillType.ALCHEMY, recipe.getXpGain());
        
        player.sendMessage("§a§l✦ Potion Crafted! §7" + recipe.getName());
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.2f);
        
        return true;
    }
    
    public boolean craftPoison(Player player, PoisonType poisonType) {
        int alchemyLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.ALCHEMY);
        
        if (alchemyLevel < 3) {
            player.sendMessage("§cYou need Alchemy level 3 to craft poisons!");
            return false;
        }
        
        // Check for basic poison ingredients
        if (!hasBasicPoisonIngredients(player, poisonType)) {
            player.sendMessage("§cYou don't have the required ingredients for this poison!");
            return false;
        }
        
        // Consume ingredients
        consumePoisonIngredients(player, poisonType);
        
        // Create poison item
        ItemStack poison = createPoisonItem(poisonType);
        player.getInventory().addItem(poison);
        
        // Award XP
        plugin.getSkillManager().addSkillXP(player, SkillType.ALCHEMY, 80);
        
        player.sendMessage("§a§l✦ Poison Crafted! §7" + poisonType.getDisplayName());
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 0.8f);
        
        return true;
    }
    
    private ItemStack createPotionItem(AlchemyRecipe recipe) {
        ItemStack potion = new ItemStack(recipe.getResult());
        ItemMeta meta = potion.getItemMeta();
        
        meta.setDisplayName("§b" + recipe.getName());
        List<String> lore = new ArrayList<>();
        lore.add(recipe.getDescription());
        lore.add("");
        lore.add("§7Crafted with Alchemy");
        lore.add("§7Right-click to consume");
        meta.setLore(lore);
        
        // Add custom potion effects for healing potions
        if (potion.getType() == Material.POTION && meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta;
            
            if (recipe.getName().contains("Healing")) {
                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, getHealingAmplifier(recipe)), true);
            } else if (recipe.getName().contains("Strength")) {
                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6000, 1), true);
            } else if (recipe.getName().contains("Speed")) {
                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 1), true);
            } else if (recipe.getName().contains("Night Vision")) {
                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9600, 0), true);
            } else if (recipe.getName().contains("Fire Resistance")) {
                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9600, 0), true);
            } else if (recipe.getName().contains("Invisibility")) {
                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 3600, 0), true);
            } else if (recipe.getName().contains("Water Breathing")) {
                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 9600, 0), true);
            } else if (recipe.getName().contains("Levitation")) {
                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 600, 0), true);
            }
        }
        
        potion.setItemMeta(meta);
        return potion;
    }
    
    private ItemStack createPoisonItem(PoisonType poisonType) {
        ItemStack poison = new ItemStack(poisonType.getIcon());
        ItemMeta meta = poison.getItemMeta();
        
        meta.setDisplayName("§2" + poisonType.getDisplayName());
        List<String> lore = new ArrayList<>();
        lore.add(poisonType.getDescription());
        lore.add("");
        lore.add("§7Weapon Coating");
        lore.add("§7Right-click weapon to apply");
        lore.add("§7Duration: 10 hits");
        meta.setLore(lore);
        
        poison.setItemMeta(meta);
        return poison;
    }
    
    private int getHealingAmplifier(AlchemyRecipe recipe) {
        if (recipe.getName().contains("Minor")) return 1;
        if (recipe.getName().contains("Greater")) return 4;
        return 2; // Regular healing
    }
    
    private boolean hasIngredients(Player player, AlchemyRecipe recipe) {
        Material[] ingredients = recipe.getIngredients();
        int[] amounts = recipe.getAmounts();
        
        for (int i = 0; i < ingredients.length; i++) {
            if (!hasEnoughItems(player, ingredients[i], amounts[i])) {
                return false;
            }
        }
        return true;
    }
    
    private boolean hasBasicPoisonIngredients(Player player, PoisonType poisonType) {
        // Basic ingredients needed for each poison type
        switch (poisonType) {
            case DEADLY_POISON:
                return hasEnoughItems(player, Material.SPIDER_EYE, 2) && 
                       hasEnoughItems(player, Material.FERMENTED_SPIDER_EYE, 1);
            case PARALYSIS_POISON:
                return hasEnoughItems(player, Material.FERMENTED_SPIDER_EYE, 1) && 
                       hasEnoughItems(player, Material.SLIME_BALL, 2);
            case WEAKNESS_POISON:
                return hasEnoughItems(player, Material.POISONOUS_POTATO, 2) && 
                       hasEnoughItems(player, Material.ROTTEN_FLESH, 1);
            case FROST_POISON:
                return hasEnoughItems(player, Material.BLUE_ICE, 1) && 
                       hasEnoughItems(player, Material.SNOWBALL, 3);
            default:
                return hasEnoughItems(player, poisonType.getIcon(), 1);
        }
    }
    
    private boolean hasEnoughItems(Player player, Material material, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count >= amount;
    }
    
    private void consumeIngredients(Player player, AlchemyRecipe recipe) {
        Material[] ingredients = recipe.getIngredients();
        int[] amounts = recipe.getAmounts();
        
        for (int i = 0; i < ingredients.length; i++) {
            removeItems(player, ingredients[i], amounts[i]);
        }
    }
    
    private void consumePoisonIngredients(Player player, PoisonType poisonType) {
        switch (poisonType) {
            case DEADLY_POISON:
                removeItems(player, Material.SPIDER_EYE, 2);
                removeItems(player, Material.FERMENTED_SPIDER_EYE, 1);
                break;
            case PARALYSIS_POISON:
                removeItems(player, Material.FERMENTED_SPIDER_EYE, 1);
                removeItems(player, Material.SLIME_BALL, 2);
                break;
            case WEAKNESS_POISON:
                removeItems(player, Material.POISONOUS_POTATO, 2);
                removeItems(player, Material.ROTTEN_FLESH, 1);
                break;
            case FROST_POISON:
                removeItems(player, Material.BLUE_ICE, 1);
                removeItems(player, Material.SNOWBALL, 3);
                break;
            default:
                removeItems(player, poisonType.getIcon(), 1);
                break;
        }
    }
    
    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == material) {
                int toRemove = Math.min(remaining, item.getAmount());
                item.setAmount(item.getAmount() - toRemove);
                remaining -= toRemove;
                
                if (item.getAmount() <= 0) {
                    contents[i] = null;
                }
            }
        }
        
        player.getInventory().setContents(contents);
    }
    
    public AlchemyRecipe[] getAvailableRecipes(Player player) {
        int alchemyLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.ALCHEMY);
        List<AlchemyRecipe> available = new ArrayList<>();
        
        for (AlchemyRecipe recipe : AlchemyRecipe.values()) {
            if (alchemyLevel >= recipe.getRequiredLevel()) {
                available.add(recipe);
            }
        }
        
        return available.toArray(new AlchemyRecipe[0]);
    }
}