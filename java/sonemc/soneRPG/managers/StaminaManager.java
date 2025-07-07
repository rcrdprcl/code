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
    private final Map<UUID, Boolean> sprintingPlayers;
    private BukkitRunnable staminaTask;
    private BukkitRunnable sprintTask;
    
    public StaminaManager(SoneRPG plugin) {
        this.plugin = plugin;
        this.playerStamina = new HashMap<>();
        this.sprintingPlayers = new HashMap<>();
        startStaminaTask();
        startSprintTask();
    }
    
    public PlayerStaminaData getPlayerStamina(Player player) {
        PlayerStaminaData data = playerStamina.computeIfAbsent(player.getUniqueId(), k -> new PlayerStaminaData());
        
        int staminaLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.STAMINA);
        double maxStamina = 100.0 + (staminaLevel * 15.0);
        data.setMaxStamina(maxStamina);
        
        return data;
    }
    
    public boolean consumeStaminaForSprint(Player player, double amount) {
        PlayerStaminaData stamina = getPlayerStamina(player);
        stamina.onSprint();
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
    
    public void onPlayerDamaged(Player player) {
        getPlayerStamina(player).onDamaged();
    }
    
    private void startStaminaTask() {
        staminaTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    PlayerStaminaData stamina = getPlayerStamina(player);
                    stamina.updateStamina();
                    
                    if (player.isSprinting() && !stamina.canSprint()) {
                        player.setSprinting(false);
                        sprintingPlayers.put(player.getUniqueId(), false);
                    }
                }
            }
        };
        staminaTask.runTaskTimer(plugin, 0L, 20L);
    }
    
    private void startSprintTask() {
        sprintTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.isSprinting()) {
                        PlayerStaminaData stamina = getPlayerStamina(player);
                        if (!stamina.consumeStamina(2.0)) {
                            player.setSprinting(false);
                            sprintingPlayers.put(player.getUniqueId(), false);
                        } else {
                            stamina.onSprint();
                        }
                    }
                }
            }
        };
        sprintTask.runTaskTimer(plugin, 0L, 20L);
    }
    
    public void cleanup() {
        if (staminaTask != null) {
            staminaTask.cancel();
        }
        if (sprintTask != null) {
            sprintTask.cancel();
        }
    }
}