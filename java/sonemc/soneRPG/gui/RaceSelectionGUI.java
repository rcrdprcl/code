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
import sonemc.soneRPG.enums.PlayerRace;

import java.util.ArrayList;
import java.util.List;

public class RaceSelectionGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public RaceSelectionGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, "§6§lChoose Your Race §a(WORKING)");
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        // Info item
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§e§lRace Selection §a(WORKING)");
        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7Choose your character's race to gain");
        infoLore.add("§7unique bonuses.");
        infoLore.add("");
        infoLore.add("§c§lWARNING: This choice is permanent!");
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        inventory.setItem(4, info);

        // Race items - arranged in rows
        int[] raceSlots = {
            10, 11, 12, 13, 14, 15, 16,  // First row
            19, 20, 21, 22, 23, 24, 25,  // Second row  
            28, 29, 30, 31, 32, 33, 34   // Third row
        };
        
        PlayerRace[] races = PlayerRace.values();
        for (int i = 0; i < races.length && i < raceSlots.length; i++) {
            PlayerRace race = races[i];
            ItemStack raceItem = createRaceItem(race);
            inventory.setItem(raceSlots[i], raceItem);
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

    private ItemStack createRaceItem(PlayerRace race) {
        ItemStack item = new ItemStack(race.getIcon());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§6§l" + race.getDisplayName());

        List<String> lore = new ArrayList<>();
        lore.add("§7" + race.getDescription());
        lore.add("");
        lore.add("§e§lRacial Abilities:");
        for (String ability : race.getRacialAbilities()) {
            lore.add(ability);
        }
        lore.add("");
        lore.add("§a§l▶ Click to select this race!");

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

        // Check if clicked item is a race
        for (PlayerRace race : PlayerRace.values()) {
            if (clickedItem.getType() == race.getIcon()) {
                handleRaceSelection(race);
                break;
            }
        }
    }

    private void handleRaceSelection(PlayerRace selectedRace) {
        // Set player race
        plugin.getRPGDataManager().getPlayerRPGData(player).setPlayerRace(selectedRace);
        plugin.getRPGDataManager().getPlayerRPGData(player).setHasChosenRace(true);

        // Apply racial bonuses
        applyRacialBonuses(selectedRace);

        player.sendMessage("§a§l✦ Race Selected! §7You are now " + selectedRace.getDisplayName() + "§7!");
        player.sendMessage("§7You have inherited the powers of your ancestors!");
        player.sendMessage("§7Use §e/rpgui §7to continue your adventure!");

        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.2f);

        player.closeInventory();
        
        // Open class selection next
        ClassSelectionGUI classGUI = new ClassSelectionGUI(plugin, player);
        classGUI.open();
    }

    private void applyRacialBonuses(PlayerRace race) {
        // Give skill bonuses based on race
        for (sonemc.soneRPG.enums.SkillType skill : race.getPreferredSkills()) {
            plugin.getSkillManager().getPlayerData(player).addSkillXP(skill, 100);
        }
        
        // Give starting skill points
        plugin.getSkillManager().getPlayerData(player).addSkillPoints(5);
    }
}