package sonemc.soneRPG.managers;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramManager {

    private final JavaPlugin plugin;
    private final Map<UUID, ArmorStand> healthHolograms;
    private BukkitRunnable updateTask;

    public HologramManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.healthHolograms = new HashMap<>();
        startUpdateTask();
    }

    public void createHealthHologram(LivingEntity entity) {
        // CRITICAL FIX: Additional safety check to prevent ArmorStand recursion
        if (entity instanceof ArmorStand || entity instanceof Player) {
            return;
        }

        if (healthHolograms.containsKey(entity.getUniqueId())) {
            return;
        }

        try {
            // IMPROVED: Longer delay and better positioning to prevent visibility issues
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (entity.isDead() || !entity.isValid()) return;

                Location loc = entity.getLocation().clone().add(0, entity.getHeight() + 0.8, 0);
                ArmorStand hologram = loc.getWorld().spawn(loc, ArmorStand.class);

                hologram.setVisible(false);
                hologram.setGravity(false);
                hologram.setCanPickupItems(false);
                hologram.setCustomNameVisible(false); // Start hidden
                hologram.setSmall(true);
                hologram.setMarker(true);
                hologram.setInvulnerable(true);
                hologram.setBasePlate(false);
                hologram.setArms(false);
                hologram.setSilent(true);

                // IMPROVED: Set initial position more precisely
                hologram.teleport(entity.getLocation().clone().add(0, entity.getHeight() + 0.8, 0));

                updateHologramText(hologram, entity);
                healthHolograms.put(entity.getUniqueId(), hologram);
            }, 10L); // Increased delay to 10 ticks (0.5 seconds)

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create hologram for entity: " + e.getMessage());
        }
    }

    public void removeHealthHologram(LivingEntity entity) {
        ArmorStand hologram = healthHolograms.remove(entity.getUniqueId());
        if (hologram != null && !hologram.isDead()) {
            hologram.remove();
        }
    }

    private void updateHologramText(ArmorStand hologram, LivingEntity entity) {
        try {
            double health = entity.getHealth();
            double maxHealth = entity.getMaxHealth();

            // Calculate health percentage
            double healthPercent = (health / maxHealth) * 100;

            // Color based on health percentage
            String color;
            if (healthPercent >= 75) {
                color = "§a"; // Green
            } else if (healthPercent >= 50) {
                color = "§e"; // Yellow
            } else if (healthPercent >= 25) {
                color = "§6"; // Orange
            } else {
                color = "§c"; // Red
            }

            // IMPROVED: Better health bar with more blocks for smoother appearance
            int filledBlocks = (int) Math.ceil((healthPercent / 100.0) * 15); // Increased to 15 blocks
            StringBuilder healthBar = new StringBuilder();

            for (int i = 0; i < 15; i++) {
                if (i < filledBlocks) {
                    healthBar.append("█");
                } else {
                    healthBar.append("░");
                }
            }

            // Get mob level from custom name
            String mobLevel = "";
            if (entity.getCustomName() != null && entity.getCustomName().contains("Lv.")) {
                String[] parts = entity.getCustomName().split(" ");
                if (parts.length > 0) {
                    mobLevel = parts[0] + " ";
                }
            }

            // IMPROVED: More compact and cleaner health display
            String healthText = String.format("%s%s§8[%s%s§8] §f%.0f%%",
                    mobLevel,
                    color,
                    color,
                    healthBar.toString(),
                    healthPercent);

            hologram.setCustomName(healthText);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to update hologram text: " + e.getMessage());
        }
    }

    private boolean isPlayerNearby(LivingEntity entity) {
        try {
            for (Player player : entity.getWorld().getPlayers()) {
                if (player.getLocation().distance(entity.getLocation()) <= 30) { // Increased range
                    return true;
                }
            }
        } catch (Exception e) {
            // Handle any distance calculation errors
            return false;
        }
        return false;
    }

    private void startUpdateTask() {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    healthHolograms.entrySet().removeIf(entry -> {
                        UUID entityId = entry.getKey();
                        ArmorStand hologram = entry.getValue();

                        // Safety check for hologram
                        if (hologram == null || hologram.isDead()) {
                            return true;
                        }

                        // Find the entity by UUID
                        Entity entity = null;
                        try {
                            for (Entity e : hologram.getWorld().getEntities()) {
                                if (e.getUniqueId().equals(entityId) && e instanceof LivingEntity && !(e instanceof ArmorStand)) {
                                    entity = e;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            // Handle world access errors
                            hologram.remove();
                            return true;
                        }

                        if (entity == null || entity.isDead()) {
                            hologram.remove();
                            return true;
                        }

                        LivingEntity livingEntity = (LivingEntity) entity;

                        // Check if any player is within range
                        boolean playerNearby = isPlayerNearby(livingEntity);

                        if (playerNearby) {
                            // Show hologram and update position
                            hologram.setCustomNameVisible(true);

                            // IMPROVED: Better position prediction and smoother movement
                            Location entityLoc = livingEntity.getLocation();
                            Vector velocity = livingEntity.getVelocity();

                            // Predict position with better accuracy
                            Location predictedLoc = entityLoc.clone().add(velocity.multiply(0.15));
                            Location newLoc = predictedLoc.add(0, livingEntity.getHeight() + 0.8, 0);

                            // IMPROVED: Smoother teleportation with smaller threshold
                            try {
                                if (hologram.getLocation().distance(newLoc) > 0.03) { // Reduced threshold
                                    hologram.teleport(newLoc);
                                }
                                updateHologramText(hologram, livingEntity);
                            } catch (Exception e) {
                                // Handle teleportation errors
                                plugin.getLogger().warning("Hologram update error: " + e.getMessage());
                            }
                        } else {
                            // Hide hologram when no players nearby
                            hologram.setCustomNameVisible(false);
                        }

                        return false;
                    });
                } catch (Exception e) {
                    plugin.getLogger().warning("Hologram update task error: " + e.getMessage());
                }
            }
        };
        updateTask.runTaskTimer(plugin, 0L, 3L); // IMPROVED: Even faster updates (3 ticks = 0.15 seconds)
    }

    public void cleanup() {
        if (updateTask != null) {
            updateTask.cancel();
        }

        for (ArmorStand hologram : healthHolograms.values()) {
            if (hologram != null && !hologram.isDead()) {
                hologram.remove();
            }
        }
        healthHolograms.clear();
    }
}