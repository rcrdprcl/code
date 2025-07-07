package sonemc.soneRPG.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.managers.ShopManager;
import sonemc.soneRPG.data.PlayerRPGData;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;
    private final String category;

    public ShopGUI(SoneRPG plugin, Player player, String category) {
        this.plugin = plugin;
        this.player = player;
        this.category = category;
        this.inventory = Bukkit.createInventory(null, 54, "§6§lShop");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private String formatCategoryName(String category) {
        switch (category) {
            case "armor": return "Armor & Enchanted Protection";
            case "consumables": return "Potions & Consumables";
            case "materials": return "Crafting Materials & Gems";
            default: return "General Goods";
        }
    }

    private void setupGUI() {
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);

        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to RPG Hub");
        backButton.setItemMeta(backMeta);
        inventory.setItem(0, backButton);

        // Player info
        ItemStack playerInfo = new ItemStack(Material.EMERALD);
        ItemMeta playerMeta = playerInfo.getItemMeta();
        playerMeta.setDisplayName("§6§lYour Septims: §f" + rpgData.getCoins());
        List<String> playerLore = new ArrayList<>();
        playerLore.add("§7§lBank Instructions:");
        playerLore.add("§a§l▶ Left Click: §7Withdraw 100");
        playerLore.add("§c§l▶ Right Click: §7Deposit 100");
        playerLore.add("");
        // Removed speech skill bonus display
        playerMeta.setLore(playerLore);
        playerInfo.setItemMeta(playerMeta);
        inventory.setItem(4, playerInfo);

        // Category buttons
        addCategoryButton(10, Material.NETHERITE_SWORD, "Weapons", "weapons");
        addCategoryButton(12, Material.NETHERITE_CHESTPLATE, "Armor", "armor");
        addCategoryButton(14, Material.POTION, "Consumables", "consumables");
        addCategoryButton(16, Material.DIAMOND, "Materials", "materials");

        // Display items based on category
        displayCategoryItems();

        // Bank instruction
        ItemStack bankInfo = new ItemStack(Material.CHEST);
        ItemMeta bankMeta = bankInfo.getItemMeta();
        bankMeta.setDisplayName("§6§lCoin Bank");
        List<String> bankLore = new ArrayList<>();
        bankLore.add("§7Left-click emerald above to withdraw 100 coins");
        bankLore.add("§7Right-click emerald above to deposit 100 coins");
        bankLore.add("");
        bankLore.add("§e§lWithdrawn coins become gold ingots!");
        bankMeta.setLore(bankLore);
        bankInfo.setItemMeta(bankMeta);
        inventory.setItem(49, bankInfo);

        // Fill empty slots
        ItemStack filler = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private void addCategoryButton(int slot, Material material, String name, String categoryId) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();

        if (categoryId.equals(category)) {
            meta.setDisplayName("§a§l" + name + " §7(Current)");
            button.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.setDisplayName("§7§l" + name);
        }

        List<String> lore = new ArrayList<>();
        lore.add("§7Click to browse " + name.toLowerCase());
        meta.setLore(lore);
        button.setItemMeta(meta);
        inventory.setItem(slot, button);
    }

    private void displayCategoryItems() {
        int[] itemSlots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int slotIndex = 0;

        for (ShopManager.ShopItem item : plugin.getShopManager().getShopItems().values()) {
            if (slotIndex >= itemSlots.length) break;

            if (isItemInCategory(item)) {
                ItemStack shopItem = createShopDisplay(item);
                inventory.setItem(itemSlots[slotIndex], shopItem);
                slotIndex++;
            }
        }
    }

    private boolean isItemInCategory(ShopManager.ShopItem item) {
        Material material = item.getMaterial();
        String materialName = material.name().toLowerCase();
        String itemName = item.getName().toLowerCase();

        switch (category) {
            case "weapons":
                return materialName.contains("sword") || materialName.contains("axe") ||
                        materialName.contains("bow") || materialName.contains("crossbow") ||
                        itemName.contains("sword") || itemName.contains("axe") || itemName.contains("bow");
            case "armor":
                return materialName.contains("helmet") || materialName.contains("chestplate") ||
                        materialName.contains("leggings") || materialName.contains("boots") ||
                        itemName.contains("helmet") || itemName.contains("armor") ||
                        itemName.contains("boots") || itemName.contains("greaves");
            case "consumables":
                return material == Material.POTION || materialName.contains("potion") ||
                        itemName.contains("potion");
            case "materials":
                return materialName.contains("ingot") || material == Material.DIAMOND ||
                        material == Material.EMERALD || materialName.contains("gem") ||
                        materialName.contains("scale") || materialName.contains("scroll") ||
                        materialName.contains("dust") || materialName.contains("crystal") ||
                        material == Material.AMETHYST_SHARD || material == Material.PRISMARINE_SHARD ||
                        material == Material.PAPER || material == Material.END_CRYSTAL;
            default:
                return true;
        }
    }

    private ItemStack createShopDisplay(ShopManager.ShopItem shopItem) {
        ItemStack display = shopItem.createItemStack(1);
        ItemMeta meta = display.getItemMeta();

        List<String> lore = new ArrayList<>(meta.getLore());
        lore.add("");

        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        int buyPrice = (int) shopItem.getBuyPrice();
        int sellPrice = (int) shopItem.getSellPrice();

        lore.add("§a§lBuy Price: §f" + buyPrice + " septims");
        lore.add("§c§lSell Price: §f" + sellPrice + " septims");
        lore.add("");
        lore.add("§e▶ Left-click to buy");
        lore.add("§e▶ Right-click to sell held item");

        meta.setLore(lore);
        display.setItemMeta(meta);
        return display;
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
        if (clickedItem.getType() == Material.ARROW && event.getSlot() == 0) {
            player.closeInventory();
            RPGMainGUI mainGUI = new RPGMainGUI(plugin, player);
            mainGUI.open();
            return;
        }

        // Handle emerald (bank) clicks
        if (clickedItem.getType() == Material.EMERALD && event.getSlot() == 4) {
            if (event.getClick() == ClickType.LEFT) {
                // Withdraw 100 coins
                if (plugin.getShopManager().withdrawCoins(player, 100)) {
                    player.sendMessage("§a§l✦ Withdrawn! §7100 septims converted to gold ingots!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);

                    // Refresh GUI
                    player.closeInventory();
                    ShopGUI newGUI = new ShopGUI(plugin, player, category);
                    newGUI.open();
                } else {
                    player.sendMessage("§c§lNot enough septims! You need 100 septims to withdraw.");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                }
            } else if (event.getClick() == ClickType.RIGHT) {
                // Deposit 100 coins (look for septim gold ingots)
                ItemStack heldItem = player.getInventory().getItemInMainHand();
                if (plugin.getShopManager().depositCoins(player, heldItem)) {
                    player.sendMessage("§a§l✦ Deposited! §7Gold ingots converted to septims!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);

                    // Refresh GUI
                    player.closeInventory();
                    ShopGUI newGUI = new ShopGUI(plugin, player, category);
                    newGUI.open();
                } else {
                    player.sendMessage("§c§lHold septim gold ingots to deposit them!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                }
            }
            return;
        }

        // Handle category buttons
        if (event.getSlot() >= 10 && event.getSlot() <= 16) {
            handleCategoryClick(event.getSlot());
            return;
        }

        // Handle item interactions
        if (event.getClick() == ClickType.RIGHT) {
            handleSellClick();
        } else if (event.getClick() == ClickType.LEFT) {
            handleItemClick(clickedItem);
        }
    }

    private void handleCategoryClick(int slot) {
        String newCategory = "weapons";
        switch (slot) {
            case 10: newCategory = "weapons"; break;
            case 12: newCategory = "armor"; break;
            case 14: newCategory = "consumables"; break;
            case 16: newCategory = "materials"; break;
        }

        if (!newCategory.equals(category)) {
            player.closeInventory();
            ShopGUI newGUI = new ShopGUI(plugin, player, newCategory);
            newGUI.open();
        }
    }

    private void handleSellClick() {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem == null || heldItem.getType() == Material.AIR) {
            player.sendMessage("§c§lHold an item in your hand to sell it!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        if (plugin.getShopManager().sellItem(player, heldItem)) {
            player.sendMessage("§a§l✦ Item Sold! §7Check your septims!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);

            // Refresh GUI
            player.closeInventory();
            ShopGUI newGUI = new ShopGUI(plugin, player, category);
            newGUI.open();
        } else {
            player.sendMessage("§c§lThis item cannot be sold here!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }

    private void handleItemClick(ItemStack clickedItem) {
        // Find the shop item
        for (ShopManager.ShopItem shopItem : plugin.getShopManager().getShopItems().values()) {
            if (clickedItem.getType() == shopItem.getMaterial()) {

                if (plugin.getShopManager().buyItem(player, shopItem.getId(), 1)) {
                    player.sendMessage("§a§l✦ Purchase Complete! §7" + shopItem.getName());
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_YES, 1.0f, 1.2f);

                    // Refresh GUI
                    player.closeInventory();
                    ShopGUI newGUI = new ShopGUI(plugin, player, category);
                    newGUI.open();
                } else {
                    player.sendMessage("§c§lYou don't have enough septims for this purchase!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                }
                break;
            }
        }
    }
}