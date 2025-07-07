package sonemc.soneRPG.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.QuestType;

public class QuestProgressListener implements Listener {

    private final SoneRPG plugin;

    public QuestProgressListener(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        
        if (!(entity.getKiller() instanceof Player)) {
            return;
        }
        
        Player killer = entity.getKiller();
        
        // Update kill quests
        plugin.getQuestManager().updateQuestProgress(killer, QuestType.KILL_MOBS, "ANY", 1);
        plugin.getQuestManager().updateQuestProgress(killer, QuestType.KILL_MOBS, entity.getType().name(), 1);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        
        // Update damage dealt quests
        plugin.getQuestManager().updateQuestProgress(player, QuestType.DEAL_DAMAGE, "", (int) event.getDamage());
    }
}