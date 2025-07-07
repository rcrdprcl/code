package sonemc.soneRPG.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerRPGData;

public class PotionListener implements Listener {

    private final SoneRPG plugin;

    public PotionListener(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item.getType() != Material.POTION) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) {
            return;
        }
        
        String potionName = meta.getDisplayName();
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        // Handle custom healing potions
        if (potionName.contains("Minor Healing")) {
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 8.0));
            player.sendMessage("§a§l✦ Healed! §7+4 hearts restored");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
        }
        else if (potionName.contains("Healing Potion") && !potionName.contains("Minor") && !potionName.contains("Greater")) {
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 16.0));
            player.sendMessage("§a§l✦ Healed! §7+8 hearts restored");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
        }
        else if (potionName.contains("Greater Healing")) {
            player.setHealth(player.getMaxHealth());
            player.sendMessage("§a§l✦ Fully Healed! §7All health restored");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
        }
        
        // Handle enhancement potions
        else if (potionName.contains("Strength Potion")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6000, 1));
            player.sendMessage("§c§l✦ Strength Enhanced! §7+25% damage for 5 minutes");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
        }
        else if (potionName.contains("Speed Potion")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 1));
            player.sendMessage("§b§l✦ Speed Enhanced! §7+30% speed for 5 minutes");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
        }
        else if (potionName.contains("Night Vision")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9600, 0));
            player.sendMessage("§e§l✦ Night Vision! §7See in the dark for 8 minutes");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
        }
        else if (potionName.contains("Fire Resistance")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9600, 0));
            player.sendMessage("§6§l✦ Fire Resistance! §7Immune to fire for 8 minutes");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
        }
        else if (potionName.contains("Invisibility")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 3600, 0));
            player.sendMessage("§8§l✦ Invisibility! §7You are invisible for 3 minutes");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
        }
        else if (potionName.contains("Water Breathing")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 9600, 0));
            player.sendMessage("§9§l✦ Water Breathing! §7Breathe underwater for 8 minutes");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
        }
        else if (potionName.contains("Levitation")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 600, 0));
            player.sendMessage("§d§l✦ Levitation! §7Float in the air for 30 seconds");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
        }
    }
}