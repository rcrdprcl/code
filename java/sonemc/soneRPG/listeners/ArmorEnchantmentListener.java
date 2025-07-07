package sonemc.soneRPG.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import org.bukkit.scheduler.BukkitRunnable;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.CustomEnchantment;
import sonemc.soneRPG.data.PlayerRPGData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorEnchantmentListener implements Listener {

    private final SoneRPG plugin;
    private final Map<UUID, Long> lastRegenTime;
    private BukkitRunnable armorEffectTask;

    public ArmorEnchantmentListener(SoneRPG plugin) {
        this.plugin = plugin;
        this.lastRegenTime = new HashMap<>();
        startArmorEffectTask();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            // Delay the armor check to ensure the item is equipped
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                updateArmorEffects(player);
            }, 1L);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        // Update armor effects when player changes items
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            updateArmorEffects(event.getPlayer());
        }, 1L);
    }

    private void startArmorEffectTask() {
        armorEffectTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    applyArmorEffects(player);
                }
            }
        };
        armorEffectTask.runTaskTimer(plugin, 0L, 100L); // Every 5 seconds
    }

    private void updateArmorEffects(Player player) {
        // Remove existing effects first
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.WATER_BREATHING);
        
        // Reset health to default
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        
        // Apply new effects
        applyArmorEffects(player);
    }

    private void applyArmorEffects(Player player) {
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        ItemStack[] armor = {
            player.getInventory().getHelmet(),
            player.getInventory().getChestplate(),
            player.getInventory().getLeggings(),
            player.getInventory().getBoots()
        };

        boolean hasHealthBoost = false;
        boolean hasSpeedBoost = false;
        boolean hasWaterBreathing = false;
        boolean hasRegeneration = false;

        for (ItemStack armorPiece : armor) {
            if (armorPiece != null) {
                CustomEnchantment enchant = plugin.getEnchantmentManager().getCustomEnchantment(armorPiece);
                if (enchant != null && enchant.isArmorEnchantment()) {
                    switch (enchant) {
                        case FORTIFY_HEALTH:
                            hasHealthBoost = true;
                            break;
                        case FORTIFY_SPEED:
                            hasSpeedBoost = true;
                            break;
                        case WATERBREATHING:
                            hasWaterBreathing = true;
                            break;
                        case REGENERATION:
                            hasRegeneration = true;
                            break;
                    }
                }
            }
        }

        // Apply effects
        if (hasHealthBoost) {
            double newMaxHealth = 20.0 * 1.2; // +20% health
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMaxHealth);
            if (player.getHealth() > newMaxHealth) {
                player.setHealth(newMaxHealth);
            }
        }

        if (hasSpeedBoost) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
        }

        if (hasWaterBreathing) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, false, false));
        }

        if (hasRegeneration) {
            long currentTime = System.currentTimeMillis();
            Long lastRegen = lastRegenTime.get(player.getUniqueId());
            
            if (lastRegen == null || currentTime - lastRegen >= 5000) { // Every 5 seconds
                double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + 2.0);
                player.setHealth(newHealth);
                lastRegenTime.put(player.getUniqueId(), currentTime);
            }
        }
    }

    public void cleanup() {
        if (armorEffectTask != null) {
            armorEffectTask.cancel();
        }
    }
}