package sonemc.soneRPG.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerRPGData;
import sonemc.soneRPG.enums.PlayerClass;
import sonemc.soneRPG.utils.ActionBarUtils;

public class ClassBonusListener implements Listener {

    private final SoneRPG plugin;

    public ClassBonusListener(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        if (rpgData.getPlayerClass() == null) {
            return;
        }

        PlayerClass playerClass = rpgData.getPlayerClass();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        
        double damageMultiplier = 1.0;
        
        switch (playerClass) {
            case WARRIOR:
                // +15% melee damage
                if (isMeleeWeapon(weapon)) {
                    damageMultiplier += 0.15;
                }
                break;
                
            case ARCHER:
                // +20% bow damage
                if (isBowWeapon(weapon)) {
                    damageMultiplier += 0.20;
                }
                break;
                
            case BERSERKER:
                // +30% damage when low health + life steal
                double healthPercent = player.getHealth() / player.getMaxHealth();
                if (healthPercent <= 0.5) {
                    damageMultiplier += 0.30;
                }
                
                // Life steal on all attacks
                double healAmount = event.getDamage() * 0.05; // 5% life steal
                double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + healAmount);
                player.setHealth(newHealth);
                break;
        }
        
        if (damageMultiplier > 1.0) {
            event.setDamage(event.getDamage() * damageMultiplier);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }

        Player player = event.getEntity().getKiller();
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        // Update kill streak
        rpgData.incrementKillStreak();
        
        // Award coins based on mob difficulty
        int mobLevel = getMobLevel(event.getEntity());
        int coins = Math.max(1, mobLevel * 2);
        rpgData.addCoins(coins);
        
        // Show coins gain in action bar
        ActionBarUtils.sendCoinsGain(player, coins);
        
        if (rpgData.getKillStreak() % 10 == 0) {
            ActionBarUtils.sendKillStreak(player, rpgData.getKillStreak());
            rpgData.addCoins(rpgData.getKillStreak() / 10);
        }
    }

    private boolean isMeleeWeapon(ItemStack weapon) {
        if (weapon == null) return false;
        String name = weapon.getType().name().toLowerCase();
        return name.contains("sword") || name.contains("axe");
    }

    private boolean isBowWeapon(ItemStack weapon) {
        if (weapon == null) return false;
        String name = weapon.getType().name().toLowerCase();
        return name.contains("bow") || name.contains("crossbow");
    }

    private int getMobLevel(org.bukkit.entity.Entity entity) {
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