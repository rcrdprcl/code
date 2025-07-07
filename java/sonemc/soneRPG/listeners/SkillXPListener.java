package sonemc.soneRPG.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
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

import java.util.Random;

public class SkillXPListener implements Listener {

    private final SoneRPG plugin;
    private final Random random;

    public SkillXPListener(SoneRPG plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        
        if (!(entity.getKiller() instanceof Player)) {
            return;
        }
        
        Player killer = entity.getKiller();
        
        // Check if this mob type is enabled in mobs.yml
        if (!plugin.getConfigManager().isMobEnabled(entity.getType())) {
            return;
        }
        
        // Get weapon type and calculate XP
        ItemStack weapon = killer.getInventory().getItemInMainHand();
        SkillType skillType = getSkillTypeFromWeapon(weapon);
        
        if (skillType == null) {
            return;
        }
        
        // Calculate XP based on mob type and difficulty
        int baseXP = plugin.getConfigManager().getMobBaseXP(entity.getType());
        int mobDifficulty = getMobDifficulty(entity);
        
        // Much reduced XP for slower leveling - reduced from original formula
        int totalXP = Math.max(1, (baseXP + mobDifficulty) / 4); // Reduced from /2 to /4
        
        // Award XP
        plugin.getSkillManager().addSkillXP(killer, skillType, totalXP);
        
        // Record statistics
        plugin.getStatisticsManager().addMobKill(killer, entity.getType(), mobDifficulty);
        
        // Calculate coins with reduced amount
        int baseCoins = Math.max(1, mobDifficulty / 2); // Reduced coins per kill
        plugin.getRPGDataManager().getPlayerRPGData(killer).addCoins(baseCoins);
        
        // Show XP gain in action bar instead of chat
        ActionBarUtils.sendXPGain(killer, skillType.getDisplayName(), totalXP);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        
        // Record damage statistics
        plugin.getStatisticsManager().addDamageDealt(player, event.getDamage());
        
        // Apply skill bonuses to damage
        SkillType skillType = getSkillTypeFromWeapon(weapon);
        if (skillType != null) {
            double bonus = plugin.getSkillManager().getSkillBonus(player, skillType);
            if (bonus > 0) {
                double newDamage = event.getDamage() * (1.0 + bonus);
                event.setDamage(newDamage);
            }
            
            // Check for instant kill chance from skills (much reduced)
            double instantKillChance = plugin.getSkillManager().getInstantKillChance(player, skillType);
            if (instantKillChance > 0 && random.nextDouble() < instantKillChance) {
                LivingEntity target = (LivingEntity) event.getEntity();
                target.setHealth(0);
                ActionBarUtils.sendInstantKill(player);
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.3f, 2.0f);
            }
        }
        
        // Random critical hit chance (reduced to 2%)
        if (random.nextDouble() < 0.02) {
            double critDamage = event.getDamage() * 1.5;
            event.setDamage(critDamage);
            ActionBarUtils.sendCriticalHit(player, critDamage);
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.2f);
        }
    }

    private SkillType getSkillTypeFromWeapon(ItemStack weapon) {
        if (weapon == null || weapon.getType() == Material.AIR) {
            return null;
        }
        
        String weaponName = weapon.getType().name().toLowerCase();
        
        if (weaponName.contains("sword")) {
            return SkillType.SWORD_DAMAGE;
        }  else if (weaponName.contains("bow")) {
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