package sonemc.soneRPG.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.utils.ActionBarUtils;

import java.util.Random;

public class MobAbilitiesListener implements Listener {

    private final SoneRPG plugin;
    private final Random random;

    public MobAbilitiesListener(SoneRPG plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // When mob attacks player
        if (event.getDamager() instanceof LivingEntity && event.getEntity() instanceof Player) {
            LivingEntity mob = (LivingEntity) event.getDamager();
            Player player = (Player) event.getEntity();

            // Skip if it's a player attacking
            if (mob instanceof Player) {
                return;
            }

            handleMobAttackAbilities(mob, player, event);
        }

        // When player attacks mob
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player player = (Player) event.getDamager();
            LivingEntity mob = (LivingEntity) event.getEntity();

            // Skip if target is a player
            if (mob instanceof Player) {
                return;
            }

            handleMobDefenseAbilities(player, mob, event);
        }
    }

    private void handleMobAttackAbilities(LivingEntity mob, Player player, EntityDamageByEntityEvent event) {
        String mobType = mob.getType().name().toLowerCase();
        int mobLevel = getMobLevel(mob);

        // Base chances - higher level mobs have better abilities
        double baseChance = Math.min(0.15, 0.05 + (mobLevel * 0.005)); // 5% base, +0.5% per level, max 15%

        // Fire-based mobs can set player on fire
        if (mobType.contains("blaze") || mobType.contains("magma") || mobType.contains("ghast")) {
            if (random.nextDouble() < baseChance + 0.05) { // +5% for fire mobs
                player.setFireTicks(100); // 5 seconds
                ActionBarUtils.sendActionBar(player, "§6§l🔥 IGNITED! §7You are burning!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_FLINTANDSTEEL_USE, 1.0f, 1.2f);
            }
        }

        // Ice/Cold mobs can slow player
        if (mobType.contains("stray") || mobType.contains("polar_bear") || mobType.contains("snow_golem")) {
            if (random.nextDouble() < baseChance) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1)); // 3 seconds
                ActionBarUtils.sendActionBar(player, "§b§l❄ FROZEN! §7You are slowed by cold!");
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_GLASS_BREAK, 1.0f, 0.8f);
            }
        }

        // Poison mobs can poison player
        if (mobType.contains("spider") || mobType.contains("witch") || mobType.contains("cave_spider")) {
            if (random.nextDouble() < baseChance) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 0)); // 4 seconds
                ActionBarUtils.sendActionBar(player, "§2§l☠ POISONED! §7Venom courses through your veins!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_SPIDER_AMBIENT, 1.0f, 0.8f);
            }
        }

        // Strong mobs can deal extra damage
        if (mobType.contains("iron_golem") || mobType.contains("ravager") || mobType.contains("piglin_brute") ||
                mobType.contains("wither_skeleton") || mobType.contains("hoglin")) {
            if (random.nextDouble() < baseChance - 0.02) { // Slightly lower chance for damage boost
                double extraDamage = event.getDamage() * (0.3 + (mobLevel * 0.02)); // 30% + 2% per level
                event.setDamage(event.getDamage() + extraDamage);
                ActionBarUtils.sendActionBar(player, "§c§l💥 CRUSHING BLOW! §7Extra damage taken!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_HURT, 1.0f, 0.8f);
            }
        }

        // Fast mobs can apply weakness
        if (mobType.contains("enderman") || mobType.contains("phantom") || mobType.contains("vex")) {
            if (random.nextDouble() < baseChance) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0)); // 5 seconds
                ActionBarUtils.sendActionBar(player, "§7§l💀 WEAKENED! §7Your strength is drained!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_WITHER_AMBIENT, 1.0f, 1.5f);
            }
        }

        // Undead mobs can apply wither
        if (mobType.contains("wither") || mobType.contains("zombie") || mobType.contains("skeleton")) {
            if (random.nextDouble() < baseChance - 0.03) { // Lower chance for wither
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0)); // 3 seconds
                ActionBarUtils.sendActionBar(player, "§8§l💀 WITHERING! §7Life force draining!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_WITHER_HURT, 1.0f, 1.2f);
            }
        }

        // Illager mobs can apply blindness
        if (mobType.contains("vindicator") || mobType.contains("evoker") || mobType.contains("pillager")) {
            if (random.nextDouble() < baseChance - 0.02) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0)); // 2 seconds
                ActionBarUtils.sendActionBar(player, "§0§l👁 BLINDED! §7You cannot see clearly!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f);
            }
        }
    }

    private void handleMobDefenseAbilities(Player player, LivingEntity mob, EntityDamageByEntityEvent event) {
        String mobType = mob.getType().name().toLowerCase();
        int mobLevel = getMobLevel(mob);

        // Base dodge chance - higher level mobs are more agile
        double dodgeChance = Math.min(0.12, 0.03 + (mobLevel * 0.003)); // 3% base, +0.3% per level, max 12%

        // Fast/agile mobs have higher dodge chance
        if (mobType.contains("enderman") || mobType.contains("phantom") || mobType.contains("vex") ||
                mobType.contains("silverfish") || mobType.contains("endermite")) {
            dodgeChance += 0.05; // +5% for agile mobs
        }

        // Elite mobs (higher level) have better dodge
        if (mobLevel >= 15) {
            dodgeChance += 0.03; // +3% for elite mobs
        }

        // Check for dodge
        if (random.nextDouble() < dodgeChance) {
            event.setCancelled(true);
            ActionBarUtils.sendActionBar(player, "§e§l⚡ DODGED! §7" + formatMobName(mobType) + " evaded your attack!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.5f);

            // Create dodge particles
            mob.getWorld().spawnParticle(org.bukkit.Particle.CLOUD,
                    mob.getLocation().add(0, 1, 0), 8, 0.5, 0.5, 0.5, 0.1);
            return;
        }

        // Armored mobs can reduce damage
        if (mobType.contains("iron_golem") || mobType.contains("ravager") || mobType.contains("wither_skeleton") ||
                mobType.contains("piglin_brute") || mobType.contains("guardian")) {
            if (random.nextDouble() < 0.08 + (mobLevel * 0.002)) { // 8% base + 0.2% per level
                double reduction = 0.25 + (mobLevel * 0.01); // 25% + 1% per level
                event.setDamage(event.getDamage() * (1.0 - reduction));
                ActionBarUtils.sendActionBar(player, "§7§l🛡 ARMORED! §7Damage reduced by thick hide!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.2f);
            }
        }

        // Magic mobs can reflect some damage
        if (mobType.contains("witch") || mobType.contains("evoker") || mobType.contains("shulker") ||
                mobType.contains("enderman")) {
            if (random.nextDouble() < 0.06 + (mobLevel * 0.001)) { // 6% base + 0.1% per level
                double reflectedDamage = event.getDamage() * (0.15 + (mobLevel * 0.005)); // 15% + 0.5% per level
                player.damage(reflectedDamage);
                ActionBarUtils.sendActionBar(player, "§5§l✨ MAGIC SHIELD! §7Damage reflected back!");
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.5f);
            }
        }

        // Regenerating mobs can heal slightly when hit
        if (mobType.contains("zombie") || mobType.contains("skeleton") || mobType.contains("wither")) {
            if (random.nextDouble() < 0.05 + (mobLevel * 0.001)) { // 5% base + 0.1% per level
                double healAmount = mob.getMaxHealth() * (0.02 + (mobLevel * 0.001)); // 2% + 0.1% per level
                mob.setHealth(Math.min(mob.getMaxHealth(), mob.getHealth() + healAmount));
                ActionBarUtils.sendActionBar(player, "§c§l❤ REGENERATION! §7" + formatMobName(mobType) + " heals!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.8f);

                // Healing particles
                mob.getWorld().spawnParticle(org.bukkit.Particle.HEART,
                        mob.getLocation().add(0, 1.5, 0), 3, 0.3, 0.3, 0.3, 0.1);
            }
        }
    }

    private int getMobLevel(LivingEntity mob) {
        String customName = mob.getCustomName();
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

    private String formatMobName(String mobType) {
        String[] words = mobType.split("_");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (formatted.length() > 0) formatted.append(" ");
            formatted.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
        }

        return formatted.toString();
    }
}