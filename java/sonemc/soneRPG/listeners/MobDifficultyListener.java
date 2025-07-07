package sonemc.soneRPG.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.ChatColor;
import sonemc.soneRPG.SoneRPG;

public class MobDifficultyListener implements Listener {
    
    private final SoneRPG plugin;
    
    public MobDifficultyListener(SoneRPG plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Allow all spawn reasons including manual spawning
        LivingEntity entity = event.getEntity();
        
        // Check if this mob type is enabled
        if (!plugin.getConfigManager().isMobEnabled(entity.getType())) {
            return;
        }
        
        // Find nearby players
        Player nearestPlayer = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (Player player : entity.getWorld().getPlayers()) {
            double distance = player.getLocation().distance(entity.getLocation());
            if (distance < nearestDistance && distance <= 50) { // Within 50 blocks
                nearestDistance = distance;
                nearestPlayer = player;
            }
        }
        
        if (nearestPlayer == null) {
            // If no player nearby, use default level 1
            applyDifficultyScaling(entity, 1);
            String difficultyColor = getDifficultyColor(1);
            entity.setCustomName(difficultyColor + "Lv.1 " + entity.getType().name().toLowerCase());
            entity.setCustomNameVisible(true);
            return;
        }
        
        // Generate mob difficulty based on nearest player's level
        int mobDifficulty = plugin.getRPGLevelManager().generateMobDifficulty(nearestPlayer);
        
        // Apply difficulty scaling
        applyDifficultyScaling(entity, mobDifficulty);
        
        // Set custom name to show difficulty
        String difficultyColor = getDifficultyColor(mobDifficulty);
        entity.setCustomName(difficultyColor + "Lv." + mobDifficulty + " " + entity.getType().name().toLowerCase());
        entity.setCustomNameVisible(true);
    }
    
    private void applyDifficultyScaling(LivingEntity entity, int difficulty) {
        // Scale health based on difficulty
        double baseHealth = entity.getMaxHealth();
        double newHealth = baseHealth * (1.0 + (difficulty - 1) * 0.2); // +20% per level
        
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newHealth);
        entity.setHealth(newHealth);
        
        // Scale attack damage if applicable
        if (entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
            double baseAttack = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue();
            double newAttack = baseAttack * (1.0 + (difficulty - 1) * 0.15); // +15% per level
            entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(newAttack);
        }
        
        // Scale movement speed slightly
        if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
            double baseSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
            double newSpeed = baseSpeed * (1.0 + (difficulty - 1) * 0.05); // +5% per level
            entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(newSpeed);
        }
    }
    
    private String getDifficultyColor(int difficulty) {
        if (difficulty <= 3) {
            return ChatColor.GREEN.toString();
        } else if (difficulty <= 6) {
            return ChatColor.YELLOW.toString();
        } else if (difficulty <= 10) {
            return ChatColor.GOLD.toString();
        } else if (difficulty <= 15) {
            return ChatColor.RED.toString();
        } else {
            return ChatColor.DARK_RED.toString();
        }
    }
}