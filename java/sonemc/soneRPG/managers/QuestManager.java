package sonemc.soneRPG.managers;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.Quest;
import sonemc.soneRPG.data.PlayerRPGData;
import sonemc.soneRPG.enums.QuestType;
import sonemc.soneRPG.enums.CustomEnchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestManager {

    private final SoneRPG plugin;
    private final List<Quest> allQuests;

    public QuestManager(SoneRPG plugin) {
        this.plugin = plugin;
        this.allQuests = new ArrayList<>();
        initializeQuests();
    }

    private void initializeQuests() {
        // Beginner Quests
        allQuests.add(new Quest("first_steps", "First Steps", "Kill 5 mobs to begin your adventure",
                QuestType.KILL_MOBS, 5, "ANY", 
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 100, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 50, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 1, "")
                ), 1, Material.WOODEN_SWORD));

        allQuests.add(new Quest("novice_fighter", "Novice Fighter", "Defeat 15 hostile creatures",
                QuestType.KILL_MOBS, 15, "ANY",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 200, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 100, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 2, "")
                ), 2, Material.STONE_SWORD));

        allQuests.add(new Quest("apprentice_warrior", "Apprentice Warrior", "Deal 2000 total damage to enemies",
                QuestType.DEAL_DAMAGE, 2000, "",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 250, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 2, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 150, "")
                ), 3, Material.IRON_SWORD));

        // Intermediate Quests
        allQuests.add(new Quest("warriors_path", "Warrior's Path", "Defeat 30 hostile mobs and reach level 5",
                QuestType.KILL_MOBS, 30, "ANY",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 400, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 3, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 200, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 1, "GOLDEN_FIST")
                ), 4, Material.IRON_SWORD));

        allQuests.add(new Quest("undead_hunter", "Undead Hunter", "Kill 20 undead creatures (zombies, skeletons)",
                QuestType.KILL_MOBS, 20, "ZOMBIE",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 350, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 2, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 1, "FIRE_DAMAGE"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 250, "")
                ), 5, Material.GOLDEN_SWORD));

        allQuests.add(new Quest("spider_slayer", "Spider Slayer", "Eliminate 15 spiders and cave spiders",
                QuestType.KILL_MOBS, 15, "SPIDER",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 300, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 2, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 1, "FROST_BITE"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 200, "")
                ), 4, Material.SPIDER_EYE));

        allQuests.add(new Quest("master_of_combat", "Master of Combat", "Deal 8000 total damage to enemies",
                QuestType.DEAL_DAMAGE, 8000, "",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 600, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 4, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 1, "RAGE"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 400, "")
                ), 7, Material.DIAMOND_SWORD));

        // Advanced Quests
        allQuests.add(new Quest("enchantment_seeker", "Enchantment Seeker", "Discover 3 different custom enchantments",
                QuestType.FIND_ENCHANTMENTS, 3, "",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 500, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 3, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 1, "BERSERKER"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 350, "")
                ), 8, Material.ENCHANTED_BOOK));

        allQuests.add(new Quest("nether_explorer", "Nether Explorer", "Kill 25 nether creatures (blazes, ghasts, piglins)",
                QuestType.KILL_MOBS, 25, "BLAZE",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 700, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 3, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 1, "FIRE_RESISTANCE"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 500, "")
                ), 10, Material.NETHERRACK));

        allQuests.add(new Quest("enderman_hunter", "Enderman Hunter", "Defeat 10 endermen",
                QuestType.KILL_MOBS, 10, "ENDERMAN",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 600, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 3, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 1, "FORTIFY_SPEED"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 450, "")
                ), 9, Material.ENDER_PEARL));

        allQuests.add(new Quest("damage_dealer", "Damage Dealer", "Deal 15000 total damage in combat",
                QuestType.DEAL_DAMAGE, 15000, "",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 800, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 4, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 1, "SHARP_EYE"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 600, "")
                ), 11, Material.DIAMOND_AXE));

        // Expert Quests
        allQuests.add(new Quest("legendary_hero", "Legendary Hero", "Reach RPG level 15 and kill 100 mobs",
                QuestType.REACH_LEVEL, 15, "",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 1200, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 6, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 2, "FORTIFY_HEALTH"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 800, "")
                ), 14, Material.NETHERITE_SWORD));

        allQuests.add(new Quest("enchantment_master", "Enchantment Master", "Discover 6 different custom enchantments",
                QuestType.FIND_ENCHANTMENTS, 6, "",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 1000, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 5, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 2, "REGENERATION"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 700, "")
                ), 12, Material.ENCHANTING_TABLE));

        allQuests.add(new Quest("ultimate_warrior", "Ultimate Warrior", "Deal 25000 total damage and reach level 18",
                QuestType.DEAL_DAMAGE, 25000, "",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 1500, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 7, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 3, "WATERBREATHING"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 1000, "")
                ), 16, Material.NETHERITE_AXE));

        allQuests.add(new Quest("mob_slayer", "Mob Slayer", "Kill 200 hostile creatures",
                QuestType.KILL_MOBS, 200, "ANY",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 1800, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 8, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 3, "FROST_RESISTANCE"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 1200, "")
                ), 18, Material.DIAMOND_SWORD));

        allQuests.add(new Quest("grandmaster", "Grandmaster", "Reach maximum RPG level 20",
                QuestType.REACH_LEVEL, 20, "",
                Arrays.asList(
                    new Quest.QuestReward(Quest.QuestReward.RewardType.XP, 2000, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.SKILL_POINTS, 10, ""),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.ENCHANTMENT, 5, "RAGE"),
                    new Quest.QuestReward(Quest.QuestReward.RewardType.COINS, 1500, "")
                ), 19, Material.BEACON));
    }

    public List<Quest> getAvailableQuests(Player player) {
        int playerLevel = plugin.getRPGLevelManager().getPlayerLevel(player);
        List<Quest> available = new ArrayList<>();
        
        for (Quest quest : allQuests) {
            if (quest.isAvailable(playerLevel)) {
                available.add(quest);
            }
        }
        
        return available;
    }

    public void acceptQuest(Player player, Quest quest) {
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        if (rpgData.getActiveQuests().size() >= 5) {
            player.sendMessage("§cYou can only have 5 active quests at a time!");
            return;
        }
        
        rpgData.getActiveQuests().put(quest.getId(), quest);
        player.sendMessage("§a§l✦ Quest Accepted! §7" + quest.getName());
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
    }

    public void updateQuestProgress(Player player, QuestType type, String data, int amount) {
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        for (Quest quest : rpgData.getActiveQuests().values()) {
            if (quest.getType() == type && !quest.isCompleted()) {
                boolean shouldUpdate = false;
                
                switch (type) {
                    case KILL_MOBS:
                        if (quest.getTargetData().equals("ANY") || quest.getTargetData().equals(data)) {
                            shouldUpdate = true;
                        }
                        break;
                    case REACH_LEVEL:
                    case FIND_ENCHANTMENTS:
                    case DEAL_DAMAGE:
                        shouldUpdate = true;
                        break;
                }
                
                if (shouldUpdate) {
                    quest.addProgress(amount);
                    if (quest.isCompleted()) {
                        player.sendMessage("§a§l✦ Quest Completed! §7" + quest.getName());
                        player.sendMessage("§7Use §e/rpgui §7→ §eQuests §7to claim your rewards!");
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                    }
                }
            }
        }
    }

    public void claimQuestRewards(Player player, Quest quest) {
        if (!quest.isCompleted() || quest.isClaimed()) {
            return;
        }
        
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        for (Quest.QuestReward reward : quest.getRewards()) {
            switch (reward.getType()) {
                case XP:
                    // Add XP to a random skill
                    plugin.getSkillManager().addSkillXP(player, 
                        sonemc.soneRPG.enums.SkillType.values()[
                            (int) (Math.random() * sonemc.soneRPG.enums.SkillType.values().length)
                        ], reward.getAmount());
                    break;
                case SKILL_POINTS:
                    plugin.getSkillManager().getPlayerData(player).addSkillPoints(reward.getAmount());
                    break;
                case ENCHANTMENT:
                    try {
                        CustomEnchantment enchant = CustomEnchantment.valueOf(reward.getData());
                        for (int i = 0; i < reward.getAmount(); i++) {
                            plugin.getEnchantmentManager().addEnchantment(player, enchant);
                        }
                    } catch (IllegalArgumentException e) {
                        // Invalid enchantment name
                    }
                    break;
                case COINS:
                    rpgData.addCoins(reward.getAmount());
                    break;
            }
        }
        
        quest.setClaimed(true);
        rpgData.getCompletedQuests().put(quest.getId(), quest);
        rpgData.getActiveQuests().remove(quest.getId());
        
        player.sendMessage("§a§l✦ Quest Rewards Claimed! §7Check your inventory and stats!");
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }
}