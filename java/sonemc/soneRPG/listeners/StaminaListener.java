package sonemc.soneRPG.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerStaminaData;

public class StaminaListener implements Listener {
    
    private final SoneRPG plugin;
    
    public StaminaListener(SoneRPG plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        
        if (event.isSprinting()) {
            if (!plugin.getStaminaManager().canSprint(player)) {
                event.setCancelled(true);
                player.sendMessage("§c§lOut of stamina! §7Wait for stamina to regenerate.");
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        
        Player player = (Player) event.getDamager();
        PlayerStaminaData stamina = plugin.getStaminaManager().getPlayerStamina(player);
        
        if (stamina.canCriticalHit() && Math.random() < 0.15) {
            if (plugin.getStaminaManager().consumeStaminaForCritical(player)) {
                double critDamage = event.getDamage() * 1.5;
                event.setDamage(critDamage);
                player.sendMessage("§c§l⚡ CRITICAL HIT! §7(" + String.format("%.1f", critDamage) + " damage)");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.2f);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        plugin.getStaminaManager().onPlayerDamaged(player);
    }
}