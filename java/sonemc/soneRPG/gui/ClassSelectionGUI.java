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
import sonemc.soneRPG.enums.PlayerClass;

import java.util.ArrayList;
import java.util.List;

public class ClassSelectionGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public ClassSelectionGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 45, "§6§lChoose Your Class");
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        // Info item
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§e§lClass Selection");
        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7Choose your RPG class to specialize");
        infoLore.add("§7your character's abilities and bonuses.");
        infoLore.add("");
        infoLore.add("§c§lWARNING: This choice is permanent!");
        infoLore.add("§7Choose wisely as you cannot change later.");
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        inventory.setItem(4, info);

        // Class items
        int[] classSlots = {20, 22, 24};
        PlayerClass[] classes = PlayerClass.values();

        for (int i = 0; i < classes.length && i < classSlots.length; i++) {
            PlayerClass playerClass = classes[i];
            ItemStack classItem = createClassItem(playerClass);
            inventory.setItem(classSlots[i], classItem);
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

    private ItemStack createClassItem(PlayerClass playerClass) {
        ItemStack item = new ItemStack(playerClass.getIcon());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§6§l" + playerClass.getDisplayName());

        List<String> lore = new ArrayList<>();
        lore.add("§7" + playerClass.getDescription());
        lore.add("");
        lore.add("§e§lClass Bonuses:");
        for (String bonus : playerClass.getBonuses()) {
            lore.add(bonus);
        }
        lore.add("");
        lore.add("§a§l▶ Click to select this class!");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
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

        // Check if clicked item is a class
        for (PlayerClass playerClass : PlayerClass.values()) {
            if (clickedItem.getType() == playerClass.getIcon()) {
                handleClassSelection(playerClass);
                break;
            }
        }
    }

    private void handleClassSelection(PlayerClass selectedClass) {
        // Set player class
        plugin.getRPGDataManager().getPlayerRPGData(player).setPlayerClass(selectedClass);
        plugin.getRPGDataManager().getPlayerRPGData(player).setHasChosenClass(true);

        // Give starting bonuses
        plugin.getSkillManager().getPlayerData(player).addSkillPoints(3);

        player.sendMessage("§a§l✦ Class Selected! §7You are now a §6" + selectedClass.getDisplayName() + "§7!");
        player.sendMessage("§7You received §e3 bonus skill points §7to start your journey!");
        player.sendMessage("§7Use §e/rpgui §7to access your RPG features!");

        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.2f);

        player.closeInventory();
    }
}