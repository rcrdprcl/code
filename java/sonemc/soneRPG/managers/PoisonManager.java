package sonemc.soneRPG.managers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.PoisonType;

import java.util.ArrayList;
import java.util.List;

public class PoisonManager {
    
    private final SoneRPG plugin;
    private final NamespacedKey poisonKey;
    private final NamespacedKey poisonChargesKey;
    
    public PoisonManager(SoneRPG plugin) {
        this.plugin = plugin;
        this.poisonKey = new NamespacedKey(plugin, "weapon_poison");
        this.poisonChargesKey = new NamespacedKey(plugin, "poison_charges");
    }
    
    public boolean applyPoisonToWeapon(Player player, ItemStack weapon, PoisonType poisonType) {
        if (weapon == null || weapon.getType() == Material.AIR) {
            return false;
        }
        
        // Check if weapon can be poisoned
        if (!canApplyPoison(weapon)) {
            player.sendMessage("§cThis weapon cannot be coated with poison!");
            return false;
        }
        
        // Check if weapon already has poison
        if (hasPoison(weapon)) {
            player.sendMessage("§cThis weapon already has a poison coating! Wait for it to wear off.");
            return false;
        }
        
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) return false;
        
        // Apply poison data
        meta.getPersistentDataContainer().set(poisonKey, PersistentDataType.STRING, poisonType.name());
        meta.getPersistentDataContainer().set(poisonChargesKey, PersistentDataType.INTEGER, 10);
        
        // Update lore
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        
        // Remove existing poison lore
        lore.removeIf(line -> line.contains("§2[Poisoned]"));
        
        // Add new poison lore
        lore.add("§2[Poisoned] " + poisonType.getDisplayName());
        lore.add("§8Charges remaining: 10");
        
        meta.setLore(lore);
        weapon.setItemMeta(meta);
        
        player.sendMessage("§a§l✦ Weapon Poisoned! §7Your weapon is now coated with " + poisonType.getDisplayName());
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.5f);
        
        return true;
    }
    
    public PoisonType getWeaponPoison(ItemStack weapon) {
        if (weapon == null || weapon.getType() == Material.AIR) {
            return null;
        }
        
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) return null;
        
        String poisonName = meta.getPersistentDataContainer().get(poisonKey, PersistentDataType.STRING);
        if (poisonName == null) return null;
        
        try {
            return PoisonType.valueOf(poisonName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    public boolean hasPoison(ItemStack weapon) {
        return getWeaponPoison(weapon) != null;
    }
    
    public void consumePoisonCharge(ItemStack weapon) {
        if (weapon == null || weapon.getType() == Material.AIR) {
            return;
        }
        
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) return;
        
        Integer charges = meta.getPersistentDataContainer().get(poisonChargesKey, PersistentDataType.INTEGER);
        if (charges == null || charges <= 0) {
            removePoison(weapon);
            return;
        }
        
        charges--;
        
        if (charges <= 0) {
            removePoison(weapon);
        } else {
            meta.getPersistentDataContainer().set(poisonChargesKey, PersistentDataType.INTEGER, charges);
            
            // Update lore
            List<String> lore = meta.getLore();
            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    if (lore.get(i).contains("Charges remaining:")) {
                        lore.set(i, "§8Charges remaining: " + charges);
                        break;
                    }
                }
                meta.setLore(lore);
            }
            
            weapon.setItemMeta(meta);
        }
    }
    
    private void removePoison(ItemStack weapon) {
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) return;
        
        // Remove poison data
        meta.getPersistentDataContainer().remove(poisonKey);
        meta.getPersistentDataContainer().remove(poisonChargesKey);
        
        // Remove poison lore
        List<String> lore = meta.getLore();
        if (lore != null) {
            lore.removeIf(line -> line.contains("§2[Poisoned]") || line.contains("Charges remaining:"));
            if (lore.isEmpty()) {
                lore = null;
            }
            meta.setLore(lore);
        }
        
        weapon.setItemMeta(meta);
    }
    
    private boolean canApplyPoison(ItemStack weapon) {
        String weaponName = weapon.getType().name().toLowerCase();
        return weaponName.contains("sword") || weaponName.contains("axe") || 
               weaponName.contains("bow") || weaponName.contains("crossbow");
    }
}