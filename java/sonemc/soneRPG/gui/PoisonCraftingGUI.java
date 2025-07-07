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
import sonemc.soneRPG.enums.PoisonType;
import sonemc.soneRPG.enums.SkillType;

import java.util.ArrayList;
import java.util.List;

public class PoisonCraftingGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public PoisonCraftingGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 45, "§2§lPoison Brewing");
        
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

        // Info item
        ItemStack info = new ItemStack(Material.CAULDRON);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§2§lPoison Brewing");
        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7Craft deadly poisons to coat your weapons");
        infoLore.add("§7Each poison lasts for 10 hits");
        infoLore.add("§7Requires Alchemy level 3+");
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        inventory.setItem(4, info);

        // Display poison recipes
        displayPoisonRecipes();

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

    private void displayPoisonRecipes() {
        int slot = 9;
        
        for (PoisonType poisonType : PoisonType.values()) {
            if (slot >= 36) break;
            
            ItemStack poisonItem = createPoisonItem(poisonType);
            inventory.setItem(slot, poisonItem);
            slot++;
        }
    }

    private ItemStack createPoisonItem(PoisonType poisonType) {
        ItemStack item = new ItemStack(poisonType.getIcon());
        ItemMeta meta = item.getItemMeta();
        
        int alchemyLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.ALCHEMY);
        boolean canCraft = alchemyLevel >= 3;
        
        if (canCraft) {
            meta.setDisplayName("§2§l" + poisonType.getDisplayName());
        } else {
            meta.setDisplayName("§c§l" + poisonType.getDisplayName() + " §7(Locked)");
        }
        
        List<String> lore = new ArrayList<>();
        lore.add(poisonType.getDescription());
        lore.add("§7Required Alchemy: §23");
        lore.add("§7XP Gain: §b+80");
        lore.add("");
        lore.add("§e§lBasic Ingredients:");
        
        // Show basic ingredients for each poison
        switch (poisonType) {
            case DEADLY_POISON:
                addIngredientLore(lore, Material.SPIDER_EYE, 2);
                addIngredientLore(lore, Material.FERMENTED_SPIDER_EYE, 1);
                break;
            case PARALYSIS_POISON:
                addIngredientLore(lore, Material.FERMENTED_SPIDER_EYE, 1);
                addIngredientLore(lore, Material.SLIME_BALL, 2);
                break;
            case WEAKNESS_POISON:
                addIngredientLore(lore, Material.POISONOUS_POTATO, 2);
                addIngredientLore(lore, Material.ROTTEN_FLESH, 1);
                break;
            case FROST_POISON:
                addIngredientLore(lore, Material.BLUE_ICE, 1);
                addIngredientLore(lore, Material.SNOWBALL, 3);
                break;
            default:
                addIngredientLore(lore, poisonType.getIcon(), 1);
                break;
        }
        
        lore.add("");
        lore.add("§7Duration: §e10 weapon hits");
        lore.add("");
        
        if (canCraft) {
            boolean hasIngredients = hasRequiredIngredients(poisonType);
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

    private void addIngredientLore(List<String> lore, Material material, int amount) {
        boolean hasEnough = hasEnoughItems(material, amount);
        String color = hasEnough ? "§a" : "§c";
        lore.add(color + "• " + amount + "x " + formatMaterialName(material));
    }

    private boolean hasRequiredIngredients(PoisonType poisonType) {
        switch (poisonType) {
            case DEADLY_POISON:
                return hasEnoughItems(Material.SPIDER_EYE, 2) && 
                       hasEnoughItems(Material.FERMENTED_SPIDER_EYE, 1);
            case PARALYSIS_POISON:
                return hasEnoughItems(Material.FERMENTED_SPIDER_EYE, 1) && 
                       hasEnoughItems(Material.SLIME_BALL, 2);
            case WEAKNESS_POISON:
                return hasEnoughItems(Material.POISONOUS_POTATO, 2) && 
                       hasEnoughItems(Material.ROTTEN_FLESH, 1);
            case FROST_POISON:
                return hasEnoughItems(Material.BLUE_ICE, 1) && 
                       hasEnoughItems(Material.SNOWBALL, 3);
            default:
                return hasEnoughItems(poisonType.getIcon(), 1);
        }
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

        // Handle poison crafting
        handlePoisonClick(clickedItem);
    }

    private void handlePoisonClick(ItemStack clickedItem) {
        String itemName = clickedItem.getItemMeta().getDisplayName();
        if (itemName.contains("(Locked)")) {
            player.sendMessage("§cYou need Alchemy level 3 to craft poisons!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Find the poison type
        for (PoisonType poisonType : PoisonType.values()) {
            if (clickedItem.getType() == poisonType.getIcon()) {
                if (plugin.getAlchemyManager().craftPoison(player, poisonType)) {
                    // Refresh GUI
                    player.closeInventory();
                    PoisonCraftingGUI newGUI = new PoisonCraftingGUI(plugin, player);
                    newGUI.open();
                } else {
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                }
                break;
            }
        }
    }
}