package sonemc.soneRPG.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ParticleEffectListener implements Listener {
    
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
        ParticleEffect effect = getParticleEffect(weapon.getType());
        if (effect == null) {
            return;
        }
        
        // Spawn particles at the damaged entity's location
        Location hitLocation = event.getEntity().getLocation().clone().add(0, 1, 0);
        spawnParticles(hitLocation, effect);
    }
    
    private ParticleEffect getParticleEffect(Material weapon) {
        String weaponName = weapon.name().toLowerCase();
        
        // Wooden tools
        if (weaponName.contains("wooden") || weaponName.contains("wood")) {
            return new ParticleEffect(Particle.BLOCK_DUST, Material.DARK_OAK_LOG.createBlockData(), 15, 0.5, 0.5, 0.5, 0.1);
        }
        // Stone tools
        else if (weaponName.contains("stone")) {
            return new ParticleEffect(Particle.BLOCK_DUST, Material.STONE.createBlockData(), 15, 0.5, 0.5, 0.5, 0.1);
        }
        // Iron tools
        else if (weaponName.contains("iron")) {
            return new ParticleEffect(Particle.CLOUD, null, 10, 0.5, 0.5, 0.5, 0.05);
        }
        // Diamond tools
        else if (weaponName.contains("diamond")) {
            return new ParticleEffect(Particle.ENCHANTMENT_TABLE, null, 20, 0.5, 0.5, 0.5, 0.1);
        }
        // Netherite tools
        else if (weaponName.contains("netherite")) {
            return new ParticleEffect(Particle.PORTAL, null, 25, 0.5, 0.5, 0.5, 0.1);
        }
        
        return null;
    }
    
    private void spawnParticles(Location location, ParticleEffect effect) {
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