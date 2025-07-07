package sonemc.soneRPG.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerRPGData;
import sonemc.soneRPG.data.PlayerStaminaData;
import sonemc.soneRPG.utils.ActionBarUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionBarUpdateListener implements Listener {

    private final SoneRPG plugin;
    private final Map<UUID, Long> lastActionBarUpdate;
    private BukkitRunnable actionBarTask;

    public ActionBarUpdateListener(SoneRPG plugin) {
        this.plugin = plugin;
        this.lastActionBarUpdate = new HashMap<>();
        startActionBarTask();
    }

    private void startActionBarTask() {
        actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    updatePlayerActionBar(player);
                }
            }
        };
        actionBarTask.runTaskTimer(plugin, 0L, 20L);
    }

    private void updatePlayerActionBar(Player player) {
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        PlayerStaminaData staminaData = plugin.getStaminaManager().getPlayerStamina(player);
        int level = plugin.getRPGLevelManager().getPlayerLevel(player);
        
        StringBuilder statusBar = new StringBuilder();
        
        statusBar.append("§7Level §a").append(level);
        if (rpgData.getPlayerClass() != null) {
            statusBar.append(" §7| §6").append(rpgData.getPlayerClass().getDisplayName());
        }
        
        double health = player.getHealth();
        double maxHealth = player.getMaxHealth();
        statusBar.append(" §7| §c❤ §f").append(String.format("%.0f", health)).append("§7/§f").append(String.format("%.0f", maxHealth));
        
        statusBar.append(" §7| §6⚜ §f").append(rpgData.getCoins());
        
        if (rpgData.getKillStreak() > 0) {
            statusBar.append(" §7| §c🗡 §f").append(rpgData.getKillStreak());
        }
        
        statusBar.append(" §7| §eStamina: ").append(staminaData.getStaminaBar());
        
        ActionBarUtils.sendActionBar(player, statusBar.toString());
    }

    public void cleanup() {
        if (actionBarTask != null) {
            actionBarTask.cancel();
        }
    }
}