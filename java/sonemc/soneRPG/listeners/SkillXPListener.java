package sonemc.soneRPG.listeners;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.SkillType;
import sonemc.soneRPG.utils.ActionBarUtils;

public class SkillXPListener implements Listener {

    private final SoneRPG plugin;

    public SkillXPListener(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        
        if (!(entity.getKiller() instanceof Player)) {
            return;
        }
        
        Player killer = entity.getKiller();
        
        if (!plugin.getConfigManager().isMobEnabled(entity.getType())) {
            return;
        }
        
        ItemStack weapon = killer.getInventory().getItemInMainHand();
        SkillType skillType = getSkillTypeFromWeapon(weapon);
        
        if (skillType == null) {
            return;
        }
        
        int baseXP = plugin.getConfigManager().getMobBaseXP(entity.getType());
        int mobDifficulty = getMobDifficulty(entity);
        
        int totalXP = Math.max(1, (baseXP + mobDifficulty) / 4);
        
        plugin.getSkillManager().addSkillXP(killer, skillType, totalXP);
        plugin.getStatisticsManager().addMobKill(killer, entity.getType(), mobDifficulty);
        
        int baseCoins = Math.max(1, mobDifficulty / 2);
        plugin.getRPGDataManager().getPlayerRPGData(killer).addCoins(baseCoins);
        
        ActionBarUtils.sendXPGain(killer, skillType.getDisplayName(), totalXP);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        
        plugin.getStatisticsManager().addDamageDealt(player, event.getDamage());
        
        SkillType skillType = getSkillTypeFromWeapon(weapon);
        if (skillType != null) {
            double bonus = plugin.getSkillManager().getSkillBonus(player, skillType);
            if (bonus > 0) {
                double newDamage = event.getDamage() * (1.0 + bonus);
                event.setDamage(newDamage);
            }
        }
    }

    private SkillType getSkillTypeFromWeapon(ItemStack weapon) {
        if (weapon == null || weapon.getType() == Material.AIR) {
            return null;
        }
        
        String weaponName = weapon.getType().name().toLowerCase();
        
        if (weaponName.contains("sword")) {
            return SkillType.SWORD_DAMAGE;
        } else if (weaponName.contains("bow")) {
            return SkillType.BOW_DAMAGE;
        }
        
        return null;
    }

    private int getMobDifficulty(LivingEntity entity) {
        String customName = entity.getCustomName();
        if (customName != null && customName.contains("Lv.")) {
            try {
                String levelStr = customName.substring(customName.indexOf("Lv.") + 3);
                levelStr = levelStr.substring(0, levelStr.indexOf(" "));
                return Integer.parseInt(levelStr);
            } catch (Exception e) {
                return 1;
            }
        }
        return 1;
    }
}