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

public class EnchantmentForgeGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final CustomEnchantment enchantment;
    private final Inventory inventory;

    public EnchantmentForgeGUI(SoneRPG plugin, Player player, CustomEnchantment enchantment) {
        this.plugin = plugin;
        this.player = player;
        this.enchantment = enchantment;
        this.inventory = Bukkit.createInventory(null, 27, "§5§lForging: " + enchantment.getDisplayName());
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to Enchants");
        backButton.setItemMeta(backMeta);
        inventory.setItem(0, backButton);
        
        // Enchantment info
        ItemStack enchantInfo = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta enchantMeta = enchantInfo.getItemMeta();
        enchantMeta.setDisplayName("§5§l" + enchantment.getDisplayName());
        List<String> enchantLore = new ArrayList<>();
        enchantLore.add("§7" + enchantment.getDescription());
        enchantLore.add("");
        enchantLore.add("§7Ready to forge this enchantment");
        enchantLore.add("§7onto your weapon or tool.");
        enchantMeta.setLore(enchantLore);
        enchantInfo.setItemMeta(enchantMeta);
        inventory.setItem(4, enchantInfo);
        
        // Anvil (forge button)
        ItemStack forge = new ItemStack(Material.ANVIL);
        ItemMeta forgeMeta = forge.getItemMeta();
        forgeMeta.setDisplayName("§6§l⚒ FORGE ENCHANTMENT");
        List<String> forgeLore = new ArrayList<>();
        forgeLore.add("§7Hold an eligible item and click to forge!");
        forgeLore.add("");
        forgeLore.add("§c§lREQUIREMENTS:");
        forgeLore.add("§7• Item must be compatible");
        forgeLore.add("§7• Item cannot have existing enchantment");
        forgeLore.add("§7• One-time use per enchantment");
        forgeLore.add("");
        forgeLore.add("§e▶ Click to forge!");
        forgeMeta.setLore(forgeLore);
        forge.setItemMeta(forgeMeta);
        inventory.setItem(13, forge);
        
        // Instruction item
        ItemStack instruction = new ItemStack(Material.PAPER);
        ItemMeta instructMeta = instruction.getItemMeta();
        instructMeta.setDisplayName("§e§lForging Instructions");
        List<String> instructLore = new ArrayList<>();
        instructLore.add("§71. Hold the item you want to enchant");
        instructLore.add("§72. Make sure it's compatible");
        instructLore.add("§73. Click the anvil to forge!");
        instructLore.add("");
        instructLore.add("§c§lWARNING:");
        instructLore.add("§7Each enchantment can only be used once!");
        instructLore.add("§7Find more from mobs to enchant again.");
        instructMeta.setLore(instructLore);
        instruction.setItemMeta(instructMeta);
        inventory.setItem(22, instruction);
        
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
            EnchantForgeGUI enchantGUI = new EnchantForgeGUI(plugin, player);
            enchantGUI.open();
            return;
        }
        
        // Forge button
        if (clickedItem.getType() == Material.ANVIL) {
            handleForgeAttempt();
        }
    }

    private void handleForgeAttempt() {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (heldItem == null || heldItem.getType() == Material.AIR) {
            player.sendMessage("§cYou must be holding an item to forge an enchantment!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Check if item is compatible
        if (!enchantment.canApplyTo(heldItem.getType())) {
            player.sendMessage("§cThis enchantment cannot be applied to " + formatMaterialName(heldItem.getType()) + "!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Check if item already has a custom enchantment
        if (plugin.getEnchantmentManager().getCustomEnchantment(heldItem) != null) {
            player.sendMessage("§cThis item already has a custom enchantment! Use §e/disenchant §cfirst.");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Check if player has this enchantment available
        if (!plugin.getEnchantmentManager().hasAvailableEnchantment(player, enchantment)) {
            player.sendMessage("§cYou don't have this enchantment available! Find it from mobs first.");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Apply the enchantment
        ItemStack enchantedItem = plugin.getEnchantmentManager().applyCustomEnchantment(heldItem, enchantment);
        player.getInventory().setItemInMainHand(enchantedItem);
        
        // Consume the enchantment
        plugin.getEnchantmentManager().consumeEnchantment(player, enchantment);
        
        player.sendMessage("§a§l✦ Enchantment Forged! §7Your " + formatMaterialName(heldItem.getType()) + " now bears the power of §5" + enchantment.getDisplayName() + "§7!");
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.5f);
        
        // Close the GUI
        player.closeInventory();
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
}