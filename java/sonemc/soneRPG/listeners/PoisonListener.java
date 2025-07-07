package sonemc.soneRPG.listeners;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.PoisonType;
import sonemc.soneRPG.utils.ActionBarUtils;

public class PoisonListener implements Listener {

    private final SoneRPG plugin;

    public PoisonListener(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (heldItem == null || heldItem.getType() == Material.AIR) {
            return;
        }
        
        // Check if player is holding a poison
        PoisonType poisonType = getPoisonType(heldItem);
        if (poisonType == null) {
            return;
        }
        
        // Check if player is holding a weapon in off-hand or has weapon in inventory
        ItemStack weapon = player.getInventory().getItemInOffHand();
        if (weapon == null || weapon.getType() == Material.AIR || !isWeapon(weapon)) {
            // Check main inventory for weapons
            weapon = findWeaponInInventory(player);
            if (weapon == null) {
                player.sendMessage("§cYou need to hold a weapon to apply poison!");
                return;
            }
        }
        
        // Apply poison to weapon
        if (plugin.getPoisonManager().applyPoisonToWeapon(player, weapon, poisonType)) {
            // Consume poison item
            heldItem.setAmount(heldItem.getAmount() - 1);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        LivingEntity target = (LivingEntity) event.getEntity();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        
        if (weapon == null || weapon.getType() == Material.AIR) {
            return;
        }
        
        // Check if weapon has poison
        PoisonType poisonType = plugin.getPoisonManager().getWeaponPoison(weapon);
        if (poisonType == null) {
            return;
        }
        
        // Apply poison effect
        applyPoisonEffect(target, poisonType, player);
        
        // Consume poison charge
        plugin.getPoisonManager().consumePoisonCharge(weapon);
    }

    private void applyPoisonEffect(LivingEntity target, PoisonType poisonType, Player player) {
        switch (poisonType) {
            case DEADLY_POISON:
                target.addPotionEffect(new PotionEffect(poisonType.getEffectType(), 
                    poisonType.getDuration(), poisonType.getAmplifier()));
                ActionBarUtils.sendActionBar(player, "§2§l☠ DEADLY POISON! §7Enemy is poisoned!");
                break;
                
            case PARALYSIS_POISON:
                target.addPotionEffect(new PotionEffect(poisonType.getEffectType(), 
                    poisonType.getDuration(), poisonType.getAmplifier()));
                ActionBarUtils.sendActionBar(player, "§8§l⚡ PARALYSIS! §7Enemy is slowed!");
                break;
                
            case WEAKNESS_POISON:
                target.addPotionEffect(new PotionEffect(poisonType.getEffectType(), 
                    poisonType.getDuration(), poisonType.getAmplifier()));
                ActionBarUtils.sendActionBar(player, "§7§l💀 WEAKNESS! §7Enemy damage reduced!");
                break;
                
            case BLINDNESS_POISON:
                target.addPotionEffect(new PotionEffect(poisonType.getEffectType(), 
                    poisonType.getDuration(), poisonType.getAmplifier()));
                ActionBarUtils.sendActionBar(player, "§0§l👁 BLINDNESS! §7Enemy cannot see!");
                break;
                
            case WITHER_POISON:
                target.addPotionEffect(new PotionEffect(poisonType.getEffectType(), 
                    poisonType.getDuration(), poisonType.getAmplifier()));
                ActionBarUtils.sendActionBar(player, "§8§l💀 WITHER! §7Enemy withers away!");
                break;
                
            case CONFUSION_POISON:
                target.addPotionEffect(new PotionEffect(poisonType.getEffectType(), 
                    poisonType.getDuration(), poisonType.getAmplifier()));
                ActionBarUtils.sendActionBar(player, "§5§l🌀 CONFUSION! §7Enemy is disoriented!");
                break;
                
            case FROST_POISON:
                target.addPotionEffect(new PotionEffect(poisonType.getEffectType(), 
                    poisonType.getDuration(), poisonType.getAmplifier()));
                ActionBarUtils.sendActionBar(player, "§b§l❄ FROST! §7Enemy is frozen!");
                break;
                
            case FIRE_POISON:
                target.setFireTicks(160); // 8 seconds of fire
                ActionBarUtils.sendActionBar(player, "§6§l🔥 FIRE POISON! §7Enemy burns!");
                break;
        }
        
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_SPLASH_POTION_BREAK, 1.0f, 0.8f);
    }

    private PoisonType getPoisonType(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return null;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        if (displayName == null) {
            return null;
        }
        
        for (PoisonType poisonType : PoisonType.values()) {
            if (displayName.contains(poisonType.getDisplayName())) {
                return poisonType;
            }
        }
        
        return null;
    }

    private boolean isWeapon(ItemStack item) {
        if (item == null) return false;
        String name = item.getType().name().toLowerCase();
        return name.contains("sword") || name.contains("axe") || 
               name.contains("bow") || name.contains("crossbow");
    }

    private ItemStack findWeaponInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isWeapon(item)) {
                return item;
            }
        }
        return null;
    }
}