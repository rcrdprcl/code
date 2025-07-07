package sonemc.soneRPG.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sonemc.soneRPG.SoneRPG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public LeaderboardGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 45, "§e§lLeaderboards");
        
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
        
        // Level leaderboard
        ItemStack levelBoard = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta levelMeta = levelBoard.getItemMeta();
        levelMeta.setDisplayName("§a§lTop Players by Level");
        List<String> levelLore = new ArrayList<>();
        levelLore.add("§7Highest RPG levels on the server:");
        levelLore.add("");
        
        List<Map.Entry<UUID, Integer>> topByLevel = plugin.getStatisticsManager().getTopPlayersByLevel();
        for (int i = 0; i < Math.min(10, topByLevel.size()); i++) {
            Map.Entry<UUID, Integer> entry = topByLevel.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
            String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";
            
            String position = "§7" + (i + 1) + ". ";
            if (i == 0) position = "§6👑 ";
            else if (i == 1) position = "§7🥈 ";
            else if (i == 2) position = "§c🥉 ";
            
            levelLore.add(position + "§f" + playerName + " §7- §aLevel " + entry.getValue());
        }
        
        if (topByLevel.isEmpty()) {
            levelLore.add("§7No data available yet!");
        }
        
        levelMeta.setLore(levelLore);
        levelBoard.setItemMeta(levelMeta);
        inventory.setItem(20, levelBoard);
        
        // Mob kills leaderboard
        ItemStack killBoard = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta killMeta = killBoard.getItemMeta();
        killMeta.setDisplayName("§c§lTop Players by Mob Kills");
        List<String> killLore = new ArrayList<>();
        killLore.add("§7Most mobs killed on the server:");
        killLore.add("");
        
        List<Map.Entry<UUID, Integer>> topByKills = plugin.getStatisticsManager().getTopPlayersByMobKills();
        for (int i = 0; i < Math.min(10, topByKills.size()); i++) {
            Map.Entry<UUID, Integer> entry = topByKills.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
            String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";
            
            String position = "§7" + (i + 1) + ". ";
            if (i == 0) position = "§6👑 ";
            else if (i == 1) position = "§7🥈 ";
            else if (i == 2) position = "§c🥉 ";
            
            killLore.add(position + "§f" + playerName + " §7- §c" + entry.getValue() + " kills");
        }
        
        if (topByKills.isEmpty()) {
            killLore.add("§7No data available yet!");
        }
        
        killMeta.setLore(killLore);
        killBoard.setItemMeta(killMeta);
        inventory.setItem(24, killBoard);
        
        // Your ranking
        ItemStack yourRank = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta rankMeta = yourRank.getItemMeta();
        rankMeta.setDisplayName("§b§lYour Ranking");
        List<String> rankLore = new ArrayList<>();
        rankLore.add("§7Your position on the leaderboards:");
        rankLore.add("");
        
        // Find player's rank in level leaderboard
        int levelRank = -1;
        for (int i = 0; i < topByLevel.size(); i++) {
            if (topByLevel.get(i).getKey().equals(player.getUniqueId())) {
                levelRank = i + 1;
                break;
            }
        }
        
        // Find player's rank in kills leaderboard
        int killRank = -1;
        for (int i = 0; i < topByKills.size(); i++) {
            if (topByKills.get(i).getKey().equals(player.getUniqueId())) {
                killRank = i + 1;
                break;
            }
        }
        
        if (levelRank != -1) {
            rankLore.add("§7Level Rank: §a#" + levelRank);
        } else {
            rankLore.add("§7Level Rank: §7Not ranked yet");
        }
        
        if (killRank != -1) {
            rankLore.add("§7Kills Rank: §c#" + killRank);
        } else {
            rankLore.add("§7Kills Rank: §7Not ranked yet");
        }
        
        rankLore.add("");
        rankLore.add("§7Keep playing to climb the ranks!");
        
        rankMeta.setLore(rankLore);
        yourRank.setItemMeta(rankMeta);
        inventory.setItem(40, yourRank);
        
        // Fill empty slots
        ItemStack filler = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
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
            RPGMainGUI mainGUI = new RPGMainGUI(plugin, player);
            mainGUI.open();
        }
    }
}