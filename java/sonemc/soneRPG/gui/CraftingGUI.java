package sonemc.soneRPG.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.managers.CraftingManager;
import sonemc.soneRPG.enums.SkillType;

import java.util.ArrayList;
import java.util.List;

public class CraftingGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public CraftingGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, "§6§lSkyforge");
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to RPG Hub");
        backButton.setItemMeta(backMeta);
        inventory.setItem(0, backButton);
        
        // Smithing info
        ItemStack smithingInfo = new ItemStack(Material.ANVIL);
        ItemMeta smithingMeta = smithingInfo.getItemMeta();
        smithingMeta.setDisplayName("§6§lSmithing Skill");
        List<String> smithingLore = new ArrayList<>();
        int smithingLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.SMITHING);
        smithingLore.add("§7Current Level: §6" + smithingLevel + "§7/§610");
        smithingLore.add("§7XP: §b" + plugin.getSkillManager().getPlayerData(player).getSkillXP(SkillType.SMITHING));
        smithingLore.add("");
        smithingLore.add("§7Craft items to gain smithing experience");
        smithingLore.add("§7Higher level unlocks better recipes");
        smithingMeta.setLore(smithingLore);
        smithingInfo.setItemMeta(smithingMeta);
        inventory.setItem(4, smithingInfo);

        // Display recipes
        int slot = 9;
        for (CraftingManager.CraftingRecipe recipe : plugin.getCraftingManager().getRecipes().values()) {
            if (slot >= 45) break;
            
            ItemStack recipeItem = createRecipeItem(recipe);
            inventory.setItem(slot, recipeItem);
            slot++;
        }

        // Fill empty slots
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private ItemStack createRecipeItem(CraftingManager.CraftingRecipe recipe) {
        ItemStack item = new ItemStack(recipe.getResult());
        ItemMeta meta = item.getItemMeta();
        
        int playerSmithingLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.SMITHING);
        boolean canCraft = playerSmithingLevel >= recipe.getRequiredLevel();
        
        if (canCraft) {
            meta.setDisplayName("§a§l" + recipe.getName());
        } else {
            meta.setDisplayName("§c§l" + recipe.getName() + " §7(Locked)");
        }
        
        List<String> lore = new ArrayList<>();
        lore.add("§7Quality: " + recipe.getRarity().getColoredName());
        lore.add("§7Required Smithing: §6" + recipe.getRequiredLevel());
        lore.add("");
        lore.add("§e§lIngredients:");
        
        for (CraftingManager.CraftingIngredient ingredient : recipe.getIngredients()) {
            boolean hasEnough = hasEnoughItems(ingredient.getMaterial(), ingredient.getAmount());
            String color = hasEnough ? "§a" : "§c";
            lore.add(color + "• " + ingredient.getAmount() + "x " + formatMaterialName(ingredient.getMaterial()));
        }
        
        lore.add("");
        if (canCraft) {
            boolean hasIngredients = hasAllIngredients(recipe);
            if (hasIngredients) {
                lore.add("§a§l▶ Click to craft!");
                item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            } else {
                lore.add("§c§lMissing ingredients");
            }
        } else {
            lore.add("§c§lSmithing level too low");
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private boolean hasEnoughItems(Material material, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count >= amount;
    }

    private boolean hasAllIngredients(CraftingManager.CraftingRecipe recipe) {
        for (CraftingManager.CraftingIngredient ingredient : recipe.getIngredients()) {
            if (!hasEnoughItems(ingredient.getMaterial(), ingredient.getAmount())) {
                return false;
            }
        }
        return true;
    }

    private String formatMaterialName(Material material) {
        String name = material.name().toLowerCase().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder formatted = new StringBuilder();
        
        for (String word : words) {
            if (formatted.length() > 0) formatted.append(" ");
            formatted.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
        }
        
        return formatted.toString();
    }

    public void open() {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player clicker = (Player) event.getWhoClicked();
        if (!clicker.equals(player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // Back button
        if (clickedItem.getType() == Material.ARROW) {
            player.closeInventory();
            RPGMainGUI mainGUI = new RPGMainGUI(plugin, player);
            mainGUI.open();
            return;
        }

        // Handle recipe crafting
        handleRecipeClick(clickedItem);
    }

    private void handleRecipeClick(ItemStack clickedItem) {
        String itemName = clickedItem.getItemMeta().getDisplayName();
        if (itemName.contains("(Locked)")) {
            player.sendMessage("§cYou need higher smithing skill to craft this item!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Find the recipe
        for (CraftingManager.CraftingRecipe recipe : plugin.getCraftingManager().getRecipes().values()) {
            if (clickedItem.getType() == recipe.getResult()) {
                if (plugin.getCraftingManager().craftItem(player, recipe.getId())) {
                    player.sendMessage("§a§l✦ Item Crafted! §7" + recipe.getName() + " has been forged!");
                    player.sendMessage("§7Your smithing skill increases!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
                    
                    // Refresh GUI
                    player.closeInventory();
                    CraftingGUI newGUI = new CraftingGUI(plugin, player);
                    newGUI.open();
                } else {
                    player.sendMessage("§cYou cannot craft this item right now!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                }
                break;
            }
        }
    }
}