package sonemc.soneRPG.managers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerStaminaData;
import sonemc.soneRPG.enums.SkillType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaminaManager {
    
    private final SoneRPG plugin;
    private final Map<UUID, PlayerStaminaData> playerStamina;
    private BukkitRunnable staminaTask;
    
    public StaminaManager(SoneRPG plugin) {
        this.plugin = plugin;
        this.playerStamina = new HashMap<>();
        startStaminaTask();
    }
    
    public PlayerStaminaData getPlayerStamina(Player player) {
        PlayerStaminaData data = playerStamina.computeIfAbsent(player.getUniqueId(), k -> new PlayerStaminaData());
        
        // Apply stamina skill bonus
        int staminaLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.STAMINA);
        double maxStamina = 100.0 + (staminaLevel * 15.0);
        data.setMaxStamina(maxStamina);
        
        return data;
    }
    
    public boolean consumeStaminaForSprint(Player player, double amount) {
        PlayerStaminaData stamina = getPlayerStamina(player);
        return stamina.consumeStamina(amount);
    }
    
    public boolean consumeStaminaForCritical(Player player) {
        PlayerStaminaData stamina = getPlayerStamina(player);
        return stamina.consumeStamina(25.0);
    }
    
    public boolean canSprint(Player player) {
        return getPlayerStamina(player).canSprint();
    }
    
    public boolean canCriticalHit(Player player) {
        return getPlayerStamina(player).canCriticalHit();
    }
    
    private void startStaminaTask() {
        staminaTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    PlayerStaminaData stamina = getPlayerStamina(player);
                    stamina.updateStamina();
                    
                    // Check if player is sprinting without stamina
                    if (player.isSprinting() && !stamina.canSprint()) {
                        player.setSprinting(false);
                    }
                }
            }
        };
        staminaTask.runTaskTimer(plugin, 0L, 20L); // Every second
    }
    
    public void cleanup() {
        if (staminaTask != null) {
            staminaTask.cancel();
        }
    }
}