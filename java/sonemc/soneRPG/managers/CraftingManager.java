package sonemc.soneRPG.managers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.ItemRarity;
import sonemc.soneRPG.enums.SkillType;
import sonemc.soneRPG.enums.CustomEnchantment;

import java.util.*;

public class CraftingManager {
    
    private final SoneRPG plugin;
    private final Map<String, CraftingRecipe> recipes;
    
    public CraftingManager(SoneRPG plugin) {
        this.plugin = plugin;
        this.recipes = new HashMap<>();
        initializeRecipes();
    }
    
    private void initializeRecipes() {
        // Basic weapons
        addRecipe("iron_sword_improved", "Improved Iron Sword", Material.IRON_SWORD, 5,
                Arrays.asList(
                    new CraftingIngredient(Material.IRON_INGOT, 2),
                    new CraftingIngredient(Material.STICK, 1)
                ), ItemRarity.UNCOMMON);
        
        addRecipe("iron_battleaxe", "Iron Battleaxe", Material.IRON_AXE, 8,
                Arrays.asList(
                    new CraftingIngredient(Material.IRON_INGOT, 3),
                    new CraftingIngredient(Material.STICK, 1)
                ), ItemRarity.UNCOMMON);
        
        // Better sword with coin bonus enchantment
        addEnchantedRecipe("better_sword", "Better Sword", Material.DIAMOND_SWORD, 25,
                Arrays.asList(
                    new CraftingIngredient(Material.DIAMOND, 3),
                    new CraftingIngredient(Material.STICK, 2),
                    new CraftingIngredient(Material.EMERALD, 1)
                ), ItemRarity.EPIC, CustomEnchantment.GOLDEN_FIST);
        
        // Armor sets
        addRecipe("reinforced_leather_armor", "Reinforced Leather Chestplate", Material.LEATHER_CHESTPLATE, 10,
                Arrays.asList(
                    new CraftingIngredient(Material.LEATHER, 8),
                    new CraftingIngredient(Material.IRON_INGOT, 2),
                    new CraftingIngredient(Material.STRING, 4)
                ), ItemRarity.UNCOMMON);
    }
    
    private void addRecipe(String id, String name, Material result, int smithingLevel, List<CraftingIngredient> ingredients, ItemRarity rarity) {
        recipes.put(id, new CraftingRecipe(id, name, result, smithingLevel, ingredients, rarity, null));
    }
    
    private void addEnchantedRecipe(String id, String name, Material result, int smithingLevel, List<CraftingIngredient> ingredients, ItemRarity rarity, CustomEnchantment enchantment) {
        recipes.put(id, new CraftingRecipe(id, name, result, smithingLevel, ingredients, rarity, enchantment));
    }
    
    public boolean craftItem(Player player, String recipeId) {
        CraftingRecipe recipe = recipes.get(recipeId);
        if (recipe == null) return false;
        
        // Check smithing level
        int playerSmithingLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.SMITHING);
        if (playerSmithingLevel < recipe.getRequiredLevel()) {
            return false;
        }
        
        // Check ingredients
        if (!hasIngredients(player, recipe)) {
            return false;
        }
        
        // Consume ingredients
        consumeIngredients(player, recipe);
        
        // Create result item
        ItemStack result = recipe.createResult();
        
        // Apply enchantment if recipe has one
        if (recipe.getEnchantment() != null) {
            result = plugin.getEnchantmentManager().applyCustomEnchantment(result, recipe.getEnchantment());
        }
        
        player.getInventory().addItem(result);
        
        // Award smithing XP
        int xpGain = recipe.getRequiredLevel() * 10;
        plugin.getSkillManager().addSkillXP(player, SkillType.SMITHING, xpGain);
        
        return true;
    }
    
    private boolean hasIngredients(Player player, CraftingRecipe recipe) {
        for (CraftingIngredient ingredient : recipe.getIngredients()) {
            if (!hasEnoughItems(player, ingredient.getMaterial(), ingredient.getAmount())) {
                return false;
            }
        }
        return true;
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
    
    private void consumeIngredients(Player player, CraftingRecipe recipe) {
        for (CraftingIngredient ingredient : recipe.getIngredients()) {
            removeItems(player, ingredient.getMaterial(), ingredient.getAmount());
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
    
    public Map<String, CraftingRecipe> getRecipes() {
        return recipes;
    }
    
    public static class CraftingRecipe {
        private final String id;
        private final String name;
        private final Material result;
        private final int requiredLevel;
        private final List<CraftingIngredient> ingredients;
        private final ItemRarity rarity;
        private final CustomEnchantment enchantment;
        
        public CraftingRecipe(String id, String name, Material result, int requiredLevel, List<CraftingIngredient> ingredients, ItemRarity rarity, CustomEnchantment enchantment) {
            this.id = id;
            this.name = name;
            this.result = result;
            this.requiredLevel = requiredLevel;
            this.ingredients = ingredients;
            this.rarity = rarity;
            this.enchantment = enchantment;
        }
        
        public ItemStack createResult() {
            ItemStack item = new ItemStack(result);
            ItemMeta meta = item.getItemMeta();
            
            meta.setDisplayName(rarity.getColor() + name);
            List<String> lore = new ArrayList<>();
            lore.add("§7Masterwork crafted item");
            lore.add("§7Quality: " + rarity.getColoredName());
            
            if (enchantment != null) {
                lore.add("§7Special: §6+3% coins from kills");
            }
            
            lore.add("§7Crafted with skill and dedication");
            meta.setLore(lore);
            
            item.setItemMeta(meta);
            return item;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public Material getResult() { return result; }
        public int getRequiredLevel() { return requiredLevel; }
        public List<CraftingIngredient> getIngredients() { return ingredients; }
        public ItemRarity getRarity() { return rarity; }
        public CustomEnchantment getEnchantment() { return enchantment; }
    }
    
    public static class CraftingIngredient {
        private final Material material;
        private final int amount;
        
        public CraftingIngredient(Material material, int amount) {
            this.material = material;
            this.amount = amount;
        }
        
        public Material getMaterial() { return material; }
        public int getAmount() { return amount; }
    }
}