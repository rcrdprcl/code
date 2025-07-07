package sonemc.soneRPG.managers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerSkillData;
import sonemc.soneRPG.data.PlayerRPGData;
import sonemc.soneRPG.enums.SkillType;
import sonemc.soneRPG.enums.PlayerRace;
import sonemc.soneRPG.utils.ActionBarUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillManager {

    private final SoneRPG plugin;
    private final Map<UUID, PlayerSkillData> playerData;

    public SkillManager(JavaPlugin plugin) {
        this.plugin = (SoneRPG) plugin;
        this.playerData = new HashMap<>();
    }

    public PlayerSkillData getPlayerData(Player player) {
        return playerData.computeIfAbsent(player.getUniqueId(), k -> new PlayerSkillData());
    }

    public void addSkillXP(Player player, SkillType skillType, int xp) {
        PlayerSkillData data = getPlayerData(player);
        PlayerRPGData rpgData = ((SoneRPG) plugin).getRPGDataManager().getPlayerRPGData(player);

        // Apply racial XP bonuses
        double xpMultiplier = 1.0;


        int bonusXP = (int) (xp * xpMultiplier);
        data.addSkillXP(skillType, bonusXP);

        // Check for level up
        checkForLevelUp(player, data);
    }

    private void checkForLevelUp(Player player, PlayerSkillData data) {
        int oldLevel = data.getRPGLevel();
        int newLevel = calculateRPGLevel(data.getTotalXP());

        if (newLevel > oldLevel) {
            data.setRPGLevel(newLevel);
            data.addSkillPoints(newLevel - oldLevel);

            // Use action bar for level up notification
            ActionBarUtils.sendLevelUp(player, newLevel);
            
            // Send additional info in chat for important milestone
            player.sendMessage("§a§l✦ LEVEL UP! §7You are now RPG level §a" + newLevel + "§7!");
            player.sendMessage("§7You gained §e" + (newLevel - oldLevel) + " §7skill point(s)!");
            player.sendMessage("§7Use §e/rpgui §7to access your RPG hub!");

            // Play level up sound
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }

    private int calculateRPGLevel(int totalXP) {
        // Much slower leveling system - significantly increased XP requirements
        int level = 1;
        int xpRequired = 500; // Increased from 200 to 500
        int currentXP = totalXP;

        while (currentXP >= xpRequired && level < 20) {
            currentXP -= xpRequired;
            level++;
            xpRequired = (int) (xpRequired * 1.6); // Increased from 1.4 to 1.6 for much slower progression
        }

        return level;
    }

    public boolean upgradeSkill(Player player, SkillType skillType) {
        PlayerSkillData data = getPlayerData(player);

        if (data.getSkillPoints() <= 0) {
            return false;
        }

        if (data.getSkillLevel(skillType) >= 10) { // Max level 10
            return false;
        }

        data.upgradeSkill(skillType);
        data.useSkillPoint();
        return true;
    }

    public double getSkillBonus(Player player, SkillType skillType) {
        PlayerSkillData data = getPlayerData(player);
        int skillLevel = data.getSkillLevel(skillType);

        switch (skillType) {
            case SWORD_DAMAGE:
                return skillLevel * 0.05; // 5% per level
            case BOW_DAMAGE:
                return skillLevel * 0.04; // 4% per level
            case LIGHT_ARMOR_SPEED:
                return skillLevel * 0.03; // 3% per level
            default:
                return 0.0;
        }
    }

    public double getInstantKillChance(Player player, SkillType skillType) {
        PlayerSkillData data = getPlayerData(player);
        int skillLevel = data.getSkillLevel(skillType);

        switch (skillType) {
            case SWORD_DAMAGE:
                return skillLevel * 0.0001; // 0.01% per level (reduced)
            case BOW_DAMAGE:
                return skillLevel * 0.00005; // 0.005% per level (reduced)
            default:
                return 0.0;
        }
    }

    public void saveAllPlayerData() {
    }
}