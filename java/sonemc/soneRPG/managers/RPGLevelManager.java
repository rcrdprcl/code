package sonemc.soneRPG.managers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import sonemc.soneRPG.SoneRPG;

import java.util.Random;

public class RPGLevelManager {
    
    private final SoneRPG plugin;
    private final Random random;
    
    public RPGLevelManager(JavaPlugin plugin) {
        this.plugin = (SoneRPG) plugin;
        this.random = new Random();
    }
    
    public int getPlayerLevel(Player player) {
        return plugin.getSkillManager().getPlayerData(player).getRPGLevel();
    }
    
    public void setPlayerLevel(Player player, int level) {
        plugin.getSkillManager().getPlayerData(player).setRPGLevel(Math.max(1, Math.min(20, level)));
    }
    
    public int generateMobDifficulty(Player nearbyPlayer) {
        int playerLevel = getPlayerLevel(nearbyPlayer);
        int mobLevel = 1;
        
        double roll = random.nextDouble() * 100;
        
        switch (playerLevel) {
            case 0:
            case 1:
                mobLevel = 1;
                break;
            case 2:
            case 3:
                if (roll < 5) mobLevel = 1;
                else if (roll < 75) mobLevel = 2;
                else mobLevel = 3;
                break;
            case 4:
            case 5:
            case 6:
                if (roll < 5) mobLevel = 6;
                else if (roll < 25) mobLevel = 5;
                else mobLevel = 4;
                break;
            case 7:
            case 8:
            case 9:
                if (roll < 10) mobLevel = 9;
                else if (roll < 40) mobLevel = 8;
                else mobLevel = 7;
                break;
            case 10:
            case 11:
            case 12:
            case 13:
                if (roll < 10) mobLevel = 13;
                else if (roll < 30) mobLevel = 12;
                else if (roll < 60) mobLevel = 11;
                else mobLevel = 10;
                break;
            case 14:
            case 15:
            case 16:
                if (roll < 20) mobLevel = 16;
                else if (roll < 70) mobLevel = 15;
                else mobLevel = 14;
                break;
            case 17:
            case 18:
            case 19:
                if (roll < 50) mobLevel = 19;
                else if (roll < 80) mobLevel = 18;
                else mobLevel = 17;
                break;
            case 20:
                if (roll < 5) mobLevel = 18;
                else if (roll < 20) mobLevel = 19;
                else mobLevel = 20;
                break;
        }
        
        return mobLevel;
    }
}