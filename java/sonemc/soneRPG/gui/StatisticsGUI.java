package sonemc.soneRPG.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public StatisticsGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 45, "§3§lCombat Statistics");
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        PlayerStatistics stats = plugin.getStatisticsManager().getPlayerStatistics(player);
        
        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to RPG Hub");
        backButton.setItemMeta(backMeta);
        inventory.setItem(0, backButton);
        
        // General stats
        ItemStack generalStats = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta generalMeta = generalStats.getItemMeta();
        generalMeta.setDisplayName("§3§lGeneral Combat Stats");
        List<String> generalLore = new ArrayList<>();
        generalLore.add("§7Total Mobs Killed: §c" + stats.getTotalMobKills());
        generalLore.add("§7Total Damage Dealt: §c" + String.format("%.1f", stats.getTotalDamageDealt()));
        generalLore.add("§7Enchantments Found: §5" + stats.getEnchantmentsFound());
        generalLore.add("§7Highest Mob Level: §6Lv." + stats.getHighestMobLevelKilled());
        generalMeta.setLore(generalLore);
        generalStats.setItemMeta(generalMeta);
        inventory.setItem(4, generalStats);
        
        // Mob kill breakdown
        ItemStack mobStats = new ItemStack(Material.ZOMBIE_HEAD);
        ItemMeta mobMeta = mobStats.getItemMeta();
        mobMeta.setDisplayName("§4§lMob Kill Breakdown");
        List<String> mobLore = new ArrayList<>();
        mobLore.add("§7Detailed mob kill statistics:");
        mobLore.add("");
        
        Map<EntityType, Integer> mobKills = stats.getMobKillsByType();
        if (mobKills.isEmpty()) {
            mobLore.add("§7No mobs killed yet!");
        } else {
            // Show top 10 most killed mobs
            mobKills.entrySet().stream()
                .sorted(Map.Entry.<EntityType, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    String mobName = formatEntityName(entry.getKey());
                    mobLore.add("§7" + mobName + ": §c" + entry.getValue());
                });
        }
        
        mobMeta.setLore(mobLore);
        mobStats.setItemMeta(mobMeta);
        inventory.setItem(22, mobStats);
        
        // Progress info
        ItemStack progress = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta progressMeta = progress.getItemMeta();
        progressMeta.setDisplayName("§e§lProgress Information");
        List<String> progressLore = new ArrayList<>();
        progressLore.add("§7RPG Level: §a" + plugin.getRPGLevelManager().getPlayerLevel(player));
        progressLore.add("§7Total XP: §b" + plugin.getSkillManager().getPlayerData(player).getTotalXP());
        progressLore.add("§7Skill Points: §e" + plugin.getSkillManager().getPlayerData(player).getSkillPoints());
        progressLore.add("");
        progressLore.add("§7Keep fighting to improve your stats!");
        progressMeta.setLore(progressLore);
        progress.setItemMeta(progressMeta);
        inventory.setItem(40, progress);
        
        // Fill empty slots
        ItemStack filler = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private String formatEntityName(EntityType entityType) {
        String name = entityType.name().toLowerCase().replace("_", " ");
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
        }
    }
}