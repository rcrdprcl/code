package sonemc.soneRPG.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.CustomEnchantment;
import sonemc.soneRPG.enums.SkillType;
import sonemc.soneRPG.utils.ActionBarUtils;
import sonemc.soneRPG.data.PlayerRPGData;

import java.util.Random;

public class EnhancedEnchantmentListener implements Listener {

    private final SoneRPG plugin;
    private final Random random;

    public EnhancedEnchantmentListener(SoneRPG plugin) {
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

        // HARDER ENCHANTMENT UNLOCKING - Significantly reduced chances
        boolean foundEnchantment = tryUnlockEnchantmentHarder(killer, mobDifficulty);

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

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Enhanced golden apple healing with Restoration skill
        if (item.getType().name().contains("GOLDEN_APPLE")) {
            int restorationLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(SkillType.RESTORATION);
            if (restorationLevel > 0) {
                double bonusHealing = restorationLevel * 0.01; // 1% per level
                double currentHealth = player.getHealth();
                double maxHealth = player.getMaxHealth();
                double healAmount = maxHealth * bonusHealing;

                player.setHealth(Math.min(maxHealth, currentHealth + healAmount));
                ActionBarUtils.sendActionBar(player, "§a§l✦ RESTORATION BONUS! §7+" + String.format("%.1f", healAmount) + " extra healing!");
            }
        }
    }

    private boolean tryUnlockEnchantmentHarder(Player player, int mobLevel) {
        for (CustomEnchantment enchant : CustomEnchantment.values()) {
            // MUCH HARDER: Triple the drop chance requirement and add level scaling
            int baseChance = enchant.getDropChance() * 4; // Quadrupled difficulty
            int adjustedChance = Math.max(baseChance, baseChance - (mobLevel * 1)); // Reduced mob level bonus

            // Additional difficulty scaling based on enchantment rarity
            if (enchant.name().contains("LEGENDARY") || enchant.name().contains("BERSERKER") ||
                    enchant.name().contains("FIRE_DAMAGE") || enchant.name().contains("RAGE")) {
                adjustedChance *= 2; // Double difficulty for rare enchants
            }

            if (random.nextInt(adjustedChance) == 0) {
                plugin.getEnchantmentManager().addEnchantment(player, enchant);
                return true;
            }
        }

        return false;
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
                handleBerserkerEnchantment(event, player, target);
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

    // Enhanced weapon enchantment handlers
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

    private void handleFrostBiteEnchantment(LivingEntity target, Player player) {
        if (random.nextInt(100) < 25) { // 25% chance
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2)); // 5 seconds slowness III
            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1)); // 5 seconds weakness II
            ActionBarUtils.sendActionBar(player, "§b§l❄ FROST BITE! §7Enemy frozen!");
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_GLASS_BREAK, 1.0f, 0.5f);
        }
    }

    private void handleBerserkerEnchantment(EntityDamageByEntityEvent event, Player player, LivingEntity target) {
        double targetHealthPercent = (target.getHealth() / target.getMaxHealth()) * 100;

        if (targetHealthPercent <= 50 && random.nextInt(100) < 3) { // 3% chance when target below 50% health
            double damage = target.getMaxHealth() * 0.25; // 25% of mob's max health
            target.damage(damage);
            ActionBarUtils.sendActionBar(player, "§4§l⚡ BERSERKER! §7Devastating blow!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 2.0f);
        }
    }

    private void handleFireDamageEnchantment(LivingEntity target, Player player) {
        if (random.nextInt(100) < 3) { // 3% chance
            target.setFireTicks(60); // 3 seconds of fire
            ActionBarUtils.sendActionBar(player, "§6§l🔥 FIRE DAMAGE! §7Enemy burns!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_FLINTANDSTEEL_USE, 1.0f, 1.2f);
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