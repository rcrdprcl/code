package sonemc.soneRPG.listeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.QuestType;

public class HolographicHealthListener implements Listener {

    private final SoneRPG plugin;

    public HolographicHealthListener(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        
        // CRITICAL FIX: Prevent infinite loop by excluding ArmorStands and Players
        if (entity instanceof Player || entity instanceof ArmorStand) {
            return;
        }
        
        // Create hologram for all other spawned creatures
        plugin.getHologramManager().createHealthHologram(entity);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        
        // Don't process ArmorStands or Players
        if (entity instanceof Player || entity instanceof ArmorStand) {
            return;
        }
        
        plugin.getHologramManager().removeHealthHologram(entity);
        
        // Update quest progress for enchantment finding
        if (entity.getKiller() instanceof Player) {
            Player killer = entity.getKiller();
            
            // Check if enchantment was found and update quest
            int enchantsBefore = plugin.getStatisticsManager().getPlayerStatistics(killer).getEnchantmentsFound();
            
            // This will be called after the enchantment listener, so we need to delay the check
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                int enchantsAfter = plugin.getStatisticsManager().getPlayerStatistics(killer).getEnchantmentsFound();
                if (enchantsAfter > enchantsBefore) {
                    plugin.getQuestManager().updateQuestProgress(killer, QuestType.FIND_ENCHANTMENTS, "", 1);
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Initialize player level if new
        if (plugin.getRPGLevelManager().getPlayerLevel(player) == 0) {
            plugin.getRPGLevelManager().setPlayerLevel(player, 1);
        }
        
        // Welcome message without forcing class selection
        if (!plugin.getRPGDataManager().getPlayerRPGData(player).hasChosenClass()) {
            player.sendMessage("§7[§bSoneRPG§7] §fWelcome to the enhanced RPG experience!");
            player.sendMessage("§7You can choose a class with §e/class §7for special bonuses!");
            player.sendMessage("§7Use §e/rpgui §7to access your RPG hub!");
        } else {
            player.sendMessage("§7[§bSoneRPG§7] §fWelcome back, §6" + 
                plugin.getRPGDataManager().getPlayerRPGData(player).getPlayerClass().getDisplayName() + "§f!");
            player.sendMessage("§7Your RPG level: §a" + plugin.getRPGLevelManager().getPlayerLevel(player));
            player.sendMessage("§7Use §e/rpgui §7to access your RPG hub!");
        }
        
        // Update level quest progress
        plugin.getQuestManager().updateQuestProgress(player, QuestType.REACH_LEVEL, "", 
            plugin.getRPGLevelManager().getPlayerLevel(player));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save player data on quit
        Player player = event.getPlayer();
        plugin.getSkillManager().getPlayerData(player); // This ensures data is loaded
        plugin.getStatisticsManager().getPlayerStatistics(player); // This ensures stats are loaded
        plugin.getRPGDataManager().savePlayerData(player); // Save RPG data
    }
}