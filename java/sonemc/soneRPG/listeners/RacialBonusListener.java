package sonemc.soneRPG.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerRPGData;
import sonemc.soneRPG.enums.PlayerRace;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RacialBonusListener implements Listener {

    private final SoneRPG plugin;
    private final Map<UUID, Integer> playerHitCounts;
    private final Map<UUID, UUID> playerTargets;

    public RacialBonusListener(SoneRPG plugin) {
        this.plugin = plugin;
        this.playerHitCounts = new HashMap<>();
        this.playerTargets = new HashMap<>();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);

        if (rpgData.getPlayerRace() == null) {
            return;
        }

        // Track hits for Nord XP bonus
        if (rpgData.getPlayerRace() == PlayerRace.NORD && event.getEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) event.getEntity();
            UUID targetId = target.getUniqueId();
            UUID playerId = player.getUniqueId();
            
            // Check if this is a new target or same target
            UUID currentTarget = playerTargets.get(playerId);
            if (currentTarget == null || !currentTarget.equals(targetId)) {
                // New target, reset hit count
                playerHitCounts.put(playerId, 1);
                playerTargets.put(playerId, targetId);
            } else {
                // Same target, increment hit count
                int hits = playerHitCounts.getOrDefault(playerId, 0) + 1;
                playerHitCounts.put(playerId, hits);
            }
        }

        PlayerRace race = rpgData.getPlayerRace();
        ItemStack weapon = player.getInventory().getItemInMainHand();

        double damageMultiplier = 1.0;

        switch (race) {
            case NORD:
                // +15% melee damage (keeping original bonus)
                if (isMeleeWeapon(weapon)) {
                    damageMultiplier += 0.15;
                }
                break;
        }

        if (damageMultiplier > 1.0) {
            event.setDamage(event.getDamage() * damageMultiplier);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);

        if (rpgData.getPlayerRace() == null) {
            return;
        }

        PlayerRace race = rpgData.getPlayerRace();
        double damageReduction = 1.0;

        switch (race) {
            case NORD:
                // +15% cold resistance
                if (event.getCause() == EntityDamageEvent.DamageCause.FREEZE) {
                    damageReduction = 0.85; // 15% reduction
                }
                break;
        }

        if (damageReduction < 1.0) {
            event.setDamage(event.getDamage() * damageReduction);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }

        Player player = event.getEntity().getKiller();
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);

        if (rpgData.getPlayerRace() == null) {
            return;
        }

        PlayerRace race = rpgData.getPlayerRace();
        UUID playerId = player.getUniqueId();

        // Calculate base coins
        int mobLevel = getMobLevel(event.getEntity());
        int baseCoins = Math.max(1, mobLevel);

        switch (race) {
            case NORD:
                // +2% coins per kill
                int nordCoins = (int) Math.ceil(baseCoins * 1.02);
                rpgData.addCoins(nordCoins - baseCoins); // Add the bonus
                
                // Check for quick kill XP bonus
                int hits = playerHitCounts.getOrDefault(playerId, 0);
                if (hits <= 3) {
                    // Give 3% XP bonus message
                    player.sendMessage("§a§l⚡ NORD BONUS! §7+3% XP from efficient kill!");
                }
                
                // Clean up tracking for this player
                playerHitCounts.remove(playerId);
                playerTargets.remove(playerId);
                break;
        }
    }

    private boolean isMeleeWeapon(ItemStack weapon) {
        if (weapon == null) return false;
        String name = weapon.getType().name().toLowerCase();
        return name.contains("sword") || name.contains("axe");
    }

    private int getMobLevel(LivingEntity entity) {
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