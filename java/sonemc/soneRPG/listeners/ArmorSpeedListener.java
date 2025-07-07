package sonemc.soneRPG.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorSpeedListener implements Listener {
    
    private final Map<UUID, Float> lastSpeedModifier = new HashMap<>();
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Calculate speed based on armor
        float speedMultiplier = calculateArmorSpeedMultiplier(player);
        
        // Check if speed modifier changed
        Float lastSpeed = lastSpeedModifier.get(player.getUniqueId());
        if (lastSpeed == null || Math.abs(lastSpeed - speedMultiplier) > 0.001f) {
            applySpeedModifier(player, speedMultiplier);
            lastSpeedModifier.put(player.getUniqueId(), speedMultiplier);
        }
    }
    
    private float calculateArmorSpeedMultiplier(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();
        
        float totalSpeedReduction = 0.0f;
        
        // Check each armor piece
        totalSpeedReduction += getArmorPieceSpeedReduction(helmet);
        totalSpeedReduction += getArmorPieceSpeedReduction(chestplate);
        totalSpeedReduction += getArmorPieceSpeedReduction(leggings);
        totalSpeedReduction += getArmorPieceSpeedReduction(boots);
        
        // Average the reduction (divide by 4 armor pieces)
        float averageReduction = totalSpeedReduction / 4.0f;
        
        return 1.0f - averageReduction;
    }
    
    private float getArmorPieceSpeedReduction(ItemStack armor) {
        if (armor == null || armor.getType() == Material.AIR) {
            return 0.0f; // No armor piece = no reduction
        }
        
        Material material = armor.getType();
        String materialName = material.name().toLowerCase();
        
        // Leather armor = 0% reduction (100% speed)
        if (materialName.contains("leather")) {
            return 0.0f;
        }
        // Chainmail armor = 0% reduction (100% speed)
        else if (materialName.contains("chainmail")) {
            return 0.0f;
        }
        // Iron armor = 5% reduction (95% speed)
        else if (materialName.contains("iron")) {
            return 0.05f;
        }
        // Diamond armor = 5% reduction (95% speed)
        else if (materialName.contains("diamond")) {
            return 0.05f;
        }
        // Netherite armor = 10% reduction (90% speed)
        else if (materialName.contains("netherite")) {
            return 0.10f;
        }
        
        return 0.0f; // Default no reduction
    }
    
    private void applySpeedModifier(Player player, float speedMultiplier) {
        // Remove existing speed effects from our plugin
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.SLOW);
        
        if (speedMultiplier < 1.0f) {
            // Apply slowness
            int amplifier = (int) Math.round((1.0f - speedMultiplier) * 10);
            amplifier = Math.min(amplifier, 5); // Cap at level 5
            
            PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, amplifier, false, false);
            player.addPotionEffect(slowness);
        } else if (speedMultiplier > 1.0f) {
            // Apply speed (though this shouldn't happen with our current setup)
            int amplifier = (int) Math.round((speedMultiplier - 1.0f) * 10);
            amplifier = Math.min(amplifier, 5); // Cap at level 5
            
            PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, amplifier, false, false);
            player.addPotionEffect(speed);
        }
    }
}