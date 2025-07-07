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
import sonemc.soneRPG.enums.AlchemyRecipe;
import sonemc.soneRPG.enums.SkillType;

import java.util.ArrayList;
import java.util.List;

public class PotionCraftingGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public PotionCraftingGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, "§b§lPotion Brewing");
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to Alchemy");
        backButton.setItemMeta(backMeta);
        inventory.setItem(0, backButton);

        // Display all potion recipes
        displayPotionRecipes();

        // Fill empty slots
        ItemStack filler = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private void displayPotionRecipes() {
        int slot = 9;
        
        for (AlchemyRecipe recipe : AlchemyRecipe.values()) {
            if (slot >= 45) break;
            if (recipe.getResult() == Material.POTION || recipe.getName().contains("Potion")) {
                ItemStack recipeItem = createRecipeItem(recipe);
                inventory.setItem(slot, recipeItem);
                slot++;
            }
        }
    }

    private ItemStack createRecipeItem(AlchemyRecipe recipe) {
        ItemStack item = new ItemStack(recipe.getResult());
        ItemMeta meta = item.getItemMeta();
        
        int alchemyLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.ALCHEMY);
        boolean canCraft = alchemyLevel >= recipe.getRequiredLevel();
        
        if (canCraft) {
            meta.setDisplayName("§b§l" + recipe.getName());
        } else {
            meta.setDisplayName("§c§l" + recipe.getName() + " §7(Locked)");
        }
        
        List<String> lore = new ArrayList<>();
        lore.add(recipe.getDescription());
        lore.add("§7Required Alchemy: §2" + recipe.getRequiredLevel());
        lore.add("§7XP Gain: §b+" + recipe.getXpGain());
        lore.add("");
        lore.add("§e§lIngredients:");
        
        Material[] ingredients = recipe.getIngredients();
        int[] amounts = recipe.getAmounts();
        
        for (int i = 0; i < ingredients.length; i++) {
            boolean hasEnough = hasEnoughItems(ingredients[i], amounts[i]);
            String color = hasEnough ? "§a" : "§c";
            lore.add(color + "• " + amounts[i] + "x " + formatMaterialName(ingredients[i]));
        }
        
        lore.add("");
        if (canCraft) {
            boolean hasIngredients = hasAllIngredients(recipe);
            if (hasIngredients) {
                lore.add("§a§l▶ Click to brew!");
                item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            } else {
                lore.add("§c§lMissing ingredients");
            }
        } else {
            lore.add("§c§lAlchemy level too low");
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

    private boolean hasAllIngredients(AlchemyRecipe recipe) {
        Material[] ingredients = recipe.getIngredients();
        int[] amounts = recipe.getAmounts();
        
        for (int i = 0; i < ingredients.length; i++) {
            if (!hasEnoughItems(ingredients[i], amounts[i])) {
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
            AlchemyGUI alchemyGUI = new AlchemyGUI(plugin, player);
            alchemyGUI.open();
            return;
        }

        // Handle recipe crafting
        handleRecipeClick(clickedItem);
    }

    private void handleRecipeClick(ItemStack clickedItem) {
        String itemName = clickedItem.getItemMeta().getDisplayName();
        if (itemName.contains("(Locked)")) {
            player.sendMessage("§cYou need higher alchemy skill to craft this potion!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Find the recipe
        for (AlchemyRecipe recipe : AlchemyRecipe.values()) {
            if (clickedItem.getType() == recipe.getResult() && 
                itemName.contains(recipe.getName().replace(" Potion", ""))) {
                if (plugin.getAlchemyManager().craftPotion(player, recipe)) {
                    // Refresh GUI
                    player.closeInventory();
                    PotionCraftingGUI newGUI = new PotionCraftingGUI(plugin, player);
                    newGUI.open();
                } else {
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                }
                break;
            }
        }
    }
}