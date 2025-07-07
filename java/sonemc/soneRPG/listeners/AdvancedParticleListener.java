package sonemc.soneRPG.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.CustomEnchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdvancedParticleListener implements Listener {
    
    private final SoneRPG plugin;
    private final Map<UUID, Long> lastParticleTime;
    private BukkitRunnable particleTask;
    
    public AdvancedParticleListener(SoneRPG plugin) {
        this.plugin = plugin;
        this.lastParticleTime = new HashMap<>();
        startParticleTask();
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        
        if (weapon == null || weapon.getType() == Material.AIR) {
            return;
        }
        
        // Get particle effect based on weapon material
        ParticleEffect effect = getWeaponParticleEffect(weapon.getType());
        if (effect != null) {
            // Spawn particles at the damaged entity's location
            Location hitLocation = event.getEntity().getLocation().clone().add(0, 1, 0);
            spawnParticles(hitLocation, effect);
        }

        // Check for enchantment particles
        CustomEnchantment enchant = plugin.getEnchantmentManager().getCustomEnchantment(weapon);
        if (enchant != null) {
            spawnEnchantmentParticles(event.getEntity().getLocation(), enchant);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }

        Player killer = event.getEntity().getKiller();
        Location deathLoc = event.getEntity().getLocation();

        // Death explosion particles
        spawnParticles(deathLoc.clone().add(0, 1, 0), 
            new ParticleEffect(Particle.EXPLOSION_LARGE, null, 3, 0.5, 0.5, 0.5, 0.1));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        long currentTime = System.currentTimeMillis();
        
        // Throttle particle effects
        Long lastTime = lastParticleTime.get(player.getUniqueId());
        if (lastTime != null && currentTime - lastTime < 500) { // 0.5 second cooldown
            return;
        }
    }

    private void startParticleTask() {
        particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    // Ambient enchantment particles
                    spawnAmbientEnchantmentParticles(player);
                }
            }
        };
        particleTask.runTaskTimer(plugin, 0L, 60L); // Every 3 seconds
    }

    private void spawnAmbientEnchantmentParticles(Player player) {
        ItemStack[] equipment = {
            player.getInventory().getItemInMainHand(),
            player.getInventory().getHelmet(),
            player.getInventory().getChestplate(),
            player.getInventory().getLeggings(),
            player.getInventory().getBoots()
        };

        for (ItemStack item : equipment) {
            if (item != null) {
                CustomEnchantment enchant = plugin.getEnchantmentManager().getCustomEnchantment(item);
                if (enchant != null) {
                    spawnAmbientParticles(player.getLocation(), enchant);
                }
            }
        }
    }

    private void spawnAmbientParticles(Location location, CustomEnchantment enchant) {
        Location particleLoc = location.clone().add(0, 1.5, 0);
        
        switch (enchant) {
            case FIRE_DAMAGE:
                spawnParticles(particleLoc, 
                    new ParticleEffect(Particle.FLAME, null, 1, 0.3, 0.3, 0.3, 0.01));
                break;
            case FROST_BITE:
                spawnParticles(particleLoc, 
                    new ParticleEffect(Particle.SNOWBALL, null, 1, 0.3, 0.3, 0.3, 0.01));
                break;
        }
    }

    private void spawnEnchantmentParticles(Location location, CustomEnchantment enchant) {
        Location particleLoc = location.clone().add(0, 1, 0);
        
        switch (enchant) {
            case RAGE:
                spawnParticles(particleLoc, 
                    new ParticleEffect(Particle.VILLAGER_ANGRY, null, 5, 0.5, 0.5, 0.5, 0.1));
                break;
            case FIRE_DAMAGE:
                spawnParticles(particleLoc, 
                    new ParticleEffect(Particle.FLAME, null, 8, 0.5, 0.5, 0.5, 0.1));
                break;
            case FROST_BITE:
                spawnParticles(particleLoc, 
                    new ParticleEffect(Particle.SNOWBALL, null, 10, 0.5, 0.5, 0.5, 0.1));
                break;
        }
    }

    private void spawnSoulParticles(Location from, Location to) {
        // Create a trail of soul particles from death location to player
        double distance = from.distance(to);
        int particles = (int) (distance * 2);
        
        for (int i = 0; i < particles; i++) {
            double ratio = (double) i / particles;
            Location particleLoc = from.clone().add(
                (to.getX() - from.getX()) * ratio,
                (to.getY() - from.getY()) * ratio + 1,
                (to.getZ() - from.getZ()) * ratio
            );
            
            spawnParticles(particleLoc, 
                new ParticleEffect(Particle.SOUL, null, 1, 0.1, 0.1, 0.1, 0.01));
        }
    }
    
    private ParticleEffect getWeaponParticleEffect(Material weapon) {
        String weaponName = weapon.name().toLowerCase();
        
        // Wooden tools
        if (weaponName.contains("wooden") || weaponName.contains("wood")) {
            return new ParticleEffect(Particle.BLOCK_DUST, Material.DARK_OAK_LOG.createBlockData(), 8, 0.3, 0.3, 0.3, 0.1);
        }
        // Stone tools
        else if (weaponName.contains("stone")) {
            return new ParticleEffect(Particle.BLOCK_DUST, Material.STONE.createBlockData(), 10, 0.3, 0.3, 0.3, 0.1);
        }
        // Iron tools
        else if (weaponName.contains("iron")) {
            return new ParticleEffect(Particle.CRIT, null, 8, 0.3, 0.3, 0.3, 0.1);
        }
        // Diamond tools
        else if (weaponName.contains("diamond")) {
            return new ParticleEffect(Particle.ENCHANTMENT_TABLE, null, 15, 0.5, 0.5, 0.5, 0.1);
        }
        // Netherite tools
        else if (weaponName.contains("netherite")) {
            return new ParticleEffect(Particle.SOUL_FIRE_FLAME, null, 12, 0.4, 0.4, 0.4, 0.1);
        }
        // Golden tools
        else if (weaponName.contains("golden") || weaponName.contains("gold")) {
            return new ParticleEffect(Particle.FALLING_DUST, Material.GOLD_BLOCK.createBlockData(), 10, 0.3, 0.3, 0.3, 0.1);
        }
        
        return null;
    }
    
    private void spawnParticles(Location location, ParticleEffect effect) {
        try {
            if (effect.data != null) {
                location.getWorld().spawnParticle(
                    effect.particle, 
                    location, 
                    effect.count, 
                    effect.offsetX, 
                    effect.offsetY, 
                    effect.offsetZ, 
                    effect.extra, 
                    effect.data
                );
            } else {
                location.getWorld().spawnParticle(
                    effect.particle, 
                    location, 
                    effect.count, 
                    effect.offsetX, 
                    effect.offsetY, 
                    effect.offsetZ, 
                    effect.extra
                );
            }
        } catch (Exception e) {
            // Silently handle particle errors
        }
    }

    public void cleanup() {
        if (particleTask != null) {
            particleTask.cancel();
        }
    }
    
    private static class ParticleEffect {
        final Particle particle;
        final Object data;
        final int count;
        final double offsetX, offsetY, offsetZ;
        final double extra;
        
        public ParticleEffect(Particle particle, Object data, int count, double offsetX, double offsetY, double offsetZ, double extra) {
            this.particle = particle;
            this.data = data;
            this.count = count;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.extra = extra;
        }
    }
}