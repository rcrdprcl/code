package sonemc.soneRPG.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBarUtils {
    
    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
    
    public static void sendXPGain(Player player, String skillName, int xp) {
        sendActionBar(player, "§7+§b" + xp + " §7" + skillName + " XP");
    }
    
    public static void sendCoinsGain(Player player, int coins) {
        sendActionBar(player, "§7+§6" + coins + " §7Coins");
    }
    
    public static void sendLevelUp(Player player, int newLevel) {
        sendActionBar(player, "§a§l✦ LEVEL UP! §7RPG Level §a" + newLevel);
    }
    
    public static void sendEnchantmentFound(Player player, String enchantName) {
        sendActionBar(player, "§5§l✦ ENCHANTMENT! §7Found §5" + enchantName);
    }
    
    public static void sendKillStreak(Player player, int streak) {
        sendActionBar(player, "§6§l⚡ KILL STREAK! §7" + streak + " kills");
    }
    
    public static void sendQuestProgress(Player player, String questName, int progress, int target) {
        sendActionBar(player, "§e§l✦ QUEST: §7" + questName + " §f(" + progress + "/" + target + ")");
    }
    
    public static void sendManaInfo(Player player, int mana, int maxMana) {
        sendActionBar(player, "§b§lMana: §f" + mana + "§7/§f" + maxMana);
    }
    
    public static void sendCriticalHit(Player player, double damage) {
        sendActionBar(player, "§c§l⚡ CRITICAL HIT! §7" + String.format("%.1f", damage) + " damage");
    }
    
    public static void sendInstantKill(Player player) {
        sendActionBar(player, "§4§l💀 INSTANT KILL! §7Devastating blow!");
    }
}