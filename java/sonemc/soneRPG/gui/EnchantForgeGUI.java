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
import sonemc.soneRPG.enums.CustomEnchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EnchantForgeGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public EnchantForgeGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, "§5§lEnchants");
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        Set<CustomEnchantment> unlockedEnchants = plugin.getEnchantmentManager().getUnlockedEnchantments(player);
        
        // Forge info item
        ItemStack forgeInfo = new ItemStack(Material.ANVIL);
        ItemMeta forgeMeta = forgeInfo.getItemMeta();
        forgeMeta.setDisplayName("§5§lMystic Forge");
        List<String> forgeLore = new ArrayList<>();
        forgeLore.add("§7Welcome to the Enchant Forge!");
        forgeLore.add("§7Here you can apply your found");
        forgeLore.add("§7enchantments to weapons and tools.");
        forgeLore.add("");
        forgeLore.add("§e§lNEW SYSTEM:");
        forgeLore.add("§7Each enchantment is consumed when used!");
        forgeLore.add("§7Find more from defeating mobs.");
        forgeLore.add("");
        forgeLore.add("§7Discovered: §e" + unlockedEnchants.size() + "§7/§e" + CustomEnchantment.values().length);
        forgeMeta.setLore(forgeLore);
        forgeInfo.setItemMeta(forgeMeta);
        inventory.setItem(4, forgeInfo);
        
        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to RPG Hub");
        List<String> backLore = new ArrayList<>();
        backLore.add("§7Return to the main menu");
        backMeta.setLore(backLore);
        backButton.setItemMeta(backMeta);
        inventory.setItem(0, backButton);
        
        // Enchantment items
        int slot = 9;
        for (CustomEnchantment enchant : CustomEnchantment.values()) {
            if (slot >= 45) break;
            
            boolean unlocked = unlockedEnchants.contains(enchant);
            int available = plugin.getEnchantmentManager().getAvailableCount(player, enchant);
            ItemStack enchantItem = createEnchantmentItem(enchant, unlocked, available);
            inventory.setItem(slot, enchantItem);
            slot++;
        }
        
        // Fill empty slots with glass panes
        ItemStack filler = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private ItemStack createEnchantmentItem(CustomEnchantment enchant, boolean unlocked, int available) {
        ItemStack item;
        
        if (unlocked && available > 0) {
            item = new ItemStack(Material.ENCHANTED_BOOK);
        } else if (unlocked) {
            item = new ItemStack(Material.BOOK);
        } else {
            item = new ItemStack(Material.BARRIER);
        }
        
        ItemMeta meta = item.getItemMeta();
        
        String color = (unlocked && available > 0) ? "§a" : unlocked ? "§6" : "§7";
        meta.setDisplayName(color + "§l" + enchant.getDisplayName());
        
        List<String> lore = new ArrayList<>();
        lore.add("§7" + enchant.getDescription());
        lore.add("");
        lore.add("§7Applicable to:");
        
        StringBuilder applicableItems = new StringBuilder("§8");
        Material[] applicable = enchant.getApplicableItems();
        for (int i = 0; i < Math.min(applicable.length, 3); i++) {
            if (i > 0) applicableItems.append(", ");
            applicableItems.append(formatMaterialName(applicable[i]));
        }
        if (applicable.length > 3) {
            applicableItems.append(" and ").append(applicable.length - 3).append(" more...");
        }
        lore.add(applicableItems.toString());
        
        lore.add("");
        lore.add("§7Drop Chance: §e1 in " + enchant.getDropChance());
        lore.add("");
        
        if (unlocked && available > 0) {
            lore.add("§a§l▶ AVAILABLE: " + available);
            lore.add("§7Click to open Forge Interface!");
            item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } else if (unlocked) {
            lore.add("§6§l⚠ DISCOVERED BUT NONE AVAILABLE");
            lore.add("§7Kill mobs to find more copies!");
        } else {
            lore.add("§c§l✖ UNDISCOVERED");
            lore.add("§7Kill mobs to discover this enchantment!");
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
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
        
        // Check for back button
        if (clickedItem.getType() == Material.ARROW) {
            player.closeInventory();
            RPGMainGUI mainGUI = new RPGMainGUI(plugin, player);
            mainGUI.open();
            return;
        }
        
        // Check if clicked item is an available enchantment
        if (clickedItem.getType() == Material.ENCHANTED_BOOK) {
            handleEnchantmentClick(clickedItem);
        }
    }

    private void handleEnchantmentClick(ItemStack enchantItem) {
        String displayName = enchantItem.getItemMeta().getDisplayName();
        
        CustomEnchantment selectedEnchant = null;
        for (CustomEnchantment enchant : CustomEnchantment.values()) {
            if (displayName.contains(enchant.getDisplayName())) {
                selectedEnchant = enchant;
                break;
            }
        }
        
        if (selectedEnchant == null) {
            return;
        }
        
        // Open the forge interface for this enchantment
        player.closeInventory();
        EnchantmentForgeGUI forgeGUI = new EnchantmentForgeGUI(plugin, player, selectedEnchant);
        forgeGUI.open();
    }
}