package sonemc.soneRPG.data;

import sonemc.soneRPG.enums.SkillType;

import java.util.HashMap;
import java.util.Map;

public class PlayerSkillData {

    private int rpgLevel;
    private int skillPoints;
    private final Map<SkillType, Integer> skillXP;
    private final Map<SkillType, Integer> skillLevels;

    public PlayerSkillData() {
        this.rpgLevel = 1;
        this.skillPoints = 0;
        this.skillXP = new HashMap<>();
        this.skillLevels = new HashMap<>();
        
        // Initialize all skills
        for (SkillType skill : SkillType.values()) {
            skillXP.put(skill, 0);
            skillLevels.put(skill, 0);
        }
    }

    public int getRPGLevel() {
        return rpgLevel;
    }

    public void setRPGLevel(int level) {
        this.rpgLevel = level;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void addSkillPoints(int points) {
        this.skillPoints += points;
    }

    public void useSkillPoint() {
        if (skillPoints > 0) {
            skillPoints--;
        }
    }

    public void addSkillXP(SkillType skillType, int xp) {
        skillXP.put(skillType, skillXP.get(skillType) + xp);
    }

    public int getSkillXP(SkillType skillType) {
        return skillXP.get(skillType);
    }

    public int getTotalXP() {
        return skillXP.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getSkillLevel(SkillType skillType) {
        return skillLevels.get(skillType);
    }

    public void upgradeSkill(SkillType skillType) {
        skillLevels.put(skillType, skillLevels.get(skillType) + 1);
    }
}