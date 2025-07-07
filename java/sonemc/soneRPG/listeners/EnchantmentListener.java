package sonemc.soneRPG.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.CustomEnchantment;
import sonemc.soneRPG.utils.ActionBarUtils;
import sonemc.soneRPG.data.PlayerRPGData;

import java.util.Random;

public class EnchantmentListener implements Listener {

    private final SoneRPG plugin;
    private final Random random;

    public EnchantmentListener(SoneRPG plugin) {
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

        // Get mob difficulty for enchantment unlock calculation
        int mobDifficulty = getMobDifficulty(entity);

        // Try to unlock enchantments with improved chances
        boolean foundEnchantment = plugin.getEnchantmentManager().tryUnlockEnchantment(killer, mobDifficulty);

        if (foundEnchantment) {
            plugin.getStatisticsManager().addEnchantmentFound(killer);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        LivingEntity target = (LivingEntity) event.getEntity();

        // Check weapon enchantments
        ItemStack weapon = player.getInventory().getItemInMainHand();
        CustomEnchantment weaponEnchant = plugin.getEnchantmentManager().getCustomEnchantment(weapon);
        if (weaponEnchant != null) {
            applyWeaponEnchantment(weaponEnchant, event, player, target);
        }

        // Check armor enchantments
        applyArmorEnchantments(player, event);
    }

    private void applyWeaponEnchantment(CustomEnchantment enchantment, EntityDamageByEntityEvent event, Player player, LivingEntity target) {
        switch (enchantment) {
            case RAGE:
                handleRageEnchantment(target, player, event);
                break;
            case SHARP_EYE:
                handleSharpEyeEnchantment(target, player, event);
                break;
            case GOLDEN_FIST:
                handleGoldenFistEnchantment(event);
                break;
            case FROST_BITE:
                handleFrostBiteEnchantment(target, player);
                break;
            case BERSERKER:
                handleBerserkerEnchantment(event, player);
                break;
            case FIRE_DAMAGE:
                handleFireDamageEnchantment(target, player);
                break;
        }
    }

    private void applyArmorEnchantments(Player player, EntityDamageByEntityEvent event) {
        // Check all armor pieces for enchantments
        ItemStack[] armor = {
            player.getInventory().getHelmet(),
            player.getInventory().getChestplate(),
            player.getInventory().getLeggings(),
            player.getInventory().getBoots()
        };

        for (ItemStack armorPiece : armor) {
            if (armorPiece != null) {
                CustomEnchantment enchant = plugin.getEnchantmentManager().getCustomEnchantment(armorPiece);
                if (enchant != null && enchant.isArmorEnchantment()) {
                    applyArmorEnchantmentEffect(enchant, player, event);
                }
            }
        }
    }

    private void applyArmorEnchantmentEffect(CustomEnchantment enchantment, Player player, EntityDamageByEntityEvent event) {
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);

        switch (enchantment) {
            case FORTIFY_HEALTH:
                // Applied when armor is equipped
                break;
            case FORTIFY_SPEED:
                // Applied when armor is equipped
                break;
            case FIRE_RESISTANCE:
                // Reduce fire damage by 50%
                if (event.getCause().name().contains("FIRE") || event.getCause().name().contains("LAVA")) {
                    event.setDamage(event.getDamage() * 0.5);
                }
                break;
            case FROST_RESISTANCE:
                // Reduce frost damage by 50%
                if (event.getCause().name().contains("FREEZE")) {
                    event.setDamage(event.getDamage() * 0.5);
                }
                break;
            case WATERBREATHING:
                // Handled in player update
                break;
            case REGENERATION:
                // Handled in player update
                break;
        }
    }

    // Weapon enchantment handlers
    private void handleRageEnchantment(LivingEntity target, Player player, EntityDamageByEntityEvent event) {
        double healthPercent = (target.getHealth() / target.getMaxHealth()) * 100;

        if (healthPercent <= 30 && random.nextInt(100) < 15) { // 15% chance
            target.setHealth(0);
            ActionBarUtils.sendActionBar(player, "§c§l⚡ RAGE! §7Berserker fury!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 2.0f);
            event.setDamage(0); // Prevent normal damage since we're killing instantly
        }
    }

    private void handleSharpEyeEnchantment(LivingEntity target, Player player, EntityDamageByEntityEvent event) {
        double healthPercent = (target.getHealth() / target.getMaxHealth()) * 100;

        if (healthPercent <= 25 && random.nextInt(100) < 8) { // 8% chance
            target.setHealth(0);
            ActionBarUtils.sendActionBar(player, "§a§l🎯 SHARP EYE! §7Perfect shot!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 2.0f);
            event.setDamage(0); // Prevent normal damage since we're killing instantly
        }
    }

    private void handleGoldenFistEnchantment(EntityDamageByEntityEvent event) {
        double newDamage = event.getDamage() * 1.12; // +12% damage
        event.setDamage(newDamage);
    }

    private void handleLightningStrikeEnchantment(LivingEntity target, Player player) {
        if (random.nextInt(100) < 10) { // 10% chance
            target.getWorld().strikeLightningEffect(target.getLocation());
            target.damage(6.0); // Additional lightning damage
            ActionBarUtils.sendActionBar(player, "§e§l⚡ LIGHTNING STRIKE! §7Thunder roars!");

            // Chain lightning to nearby enemies
            for (LivingEntity nearby : target.getLocation().getNearbyLivingEntities(3)) {
                if (nearby != target && nearby != player && !(nearby instanceof Player)) {
                    nearby.damage(3.0);
                }
            }
        }
    }

    private void handleFrostBiteEnchantment(LivingEntity target, Player player) {
        if (random.nextInt(100) < 25) { // 25% chance
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2)); // 5 seconds slowness III
            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1)); // 5 seconds weakness II
            ActionBarUtils.sendActionBar(player, "§b§l❄ FROST BITE! §7Enemy frozen!");
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_GLASS_BREAK, 1.0f, 0.5f);
        }
    }

    private void handlePiercingShotEnchantment(LivingEntity target, Player player) {
        if (random.nextInt(100) < 30) { // 30% chance
            ActionBarUtils.sendActionBar(player, "§d§l➤ PIERCING SHOT! §7Arrow pierces through!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.5f);

            // Damage enemies behind the target
            for (LivingEntity nearby : target.getLocation().getNearbyLivingEntities(5)) {
                if (nearby != target && nearby != player && !(nearby instanceof Player)) {
                    nearby.damage(4.0);
                }
            }
        }
    }

    private void handleBerserkerEnchantment(EntityDamageByEntityEvent event, Player player) {
        double healthPercent = (player.getHealth() / player.getMaxHealth()) * 100;

        if (healthPercent <= 50) {
            double newDamage = event.getDamage() * 1.20; // +20% damage when low health
            event.setDamage(newDamage);
            ActionBarUtils.sendActionBar(player, "§4§l⚡ BERSERKER! §7Rage fuels your strength!");
        }
    }

    private void handleVampireEnchantment(EntityDamageByEntityEvent event, Player player) {
        double healAmount = event.getDamage() * 0.08; // 8% life steal
        double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + healAmount);
        player.setHealth(newHealth);

        if (healAmount > 0.5) { // Only show message for meaningful healing
            ActionBarUtils.sendActionBar(player, "§5§l🩸 VAMPIRE! §7+" + String.format("%.1f", healAmount) + " health!");
        }
    }

    private void handleFireDamageEnchantment(LivingEntity target, Player player) {
        if (random.nextInt(100) < 20) { // 20% chance
            target.setFireTicks(160); // 8 seconds of fire
            ActionBarUtils.sendActionBar(player, "§6§l🔥 FIRE DAMAGE! §7Enemy burns!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_FLINTANDSTEEL_USE, 1.0f, 1.2f);
        }
    }

    private void handleShockDamageEnchantment(LivingEntity target, Player player) {
        if (random.nextInt(100) < 18) { // 18% chance
            ActionBarUtils.sendActionBar(player, "§9§l⚡ SHOCK DAMAGE! §7Chain lightning!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 1.5f);

            // Chain lightning to nearby enemies
            for (LivingEntity nearby : target.getLocation().getNearbyLivingEntities(4)) {
                if (nearby != target && nearby != player && !(nearby instanceof Player)) {
                    nearby.damage(5.0);
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1)); // 2 seconds slow
                }
            }
        }
    }

    private void handleParalysisEnchantment(LivingEntity target, Player player) {
        if (random.nextInt(100) < 12) { // 12% chance
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 10)); // 3 seconds complete paralysis
            target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, -10)); // Can't jump
            ActionBarUtils.sendActionBar(player, "§7§l💀 PARALYSIS! §7Enemy cannot move!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_SPIDER_AMBIENT, 1.0f, 0.5f);
        }
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