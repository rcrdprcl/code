package sonemc.soneRPG.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerSkillData;
import sonemc.soneRPG.data.PlayerStatistics;
import sonemc.soneRPG.data.PlayerRPGData;

import java.util.ArrayList;
import java.util.List;

public class RPGMainGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public RPGMainGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, "§b§lRPG Hub");
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        PlayerSkillData skillData = plugin.getSkillManager().getPlayerData(player);
        PlayerStatistics stats = plugin.getStatisticsManager().getPlayerStatistics(player);
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        // Player info (center top)
        ItemStack playerInfo = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta playerMeta = playerInfo.getItemMeta();
        playerMeta.setDisplayName("§b§l" + player.getName() + "'s Profile");
        List<String> playerLore = new ArrayList<>();
        playerLore.add("§7RPG Level: §a" + skillData.getRPGLevel() + "§7/§a20");
        playerLore.add("§7Race: " + (rpgData.getPlayerRace() != null ? "§6" + rpgData.getPlayerRace().getDisplayName() : "§7None (§e/race§7)"));
        playerLore.add("§7Class: " + (rpgData.getPlayerClass() != null ? "§6" + rpgData.getPlayerClass().getDisplayName() : "§7None (§e/class§7)"));
        playerLore.add("§7Skill Points: §e" + skillData.getSkillPoints());
        playerLore.add("§7Septims: §6" + rpgData.getCoins());
        playerLore.add("§7Health: §c" + String.format("%.0f", player.getHealth()) + "§7/§c" + String.format("%.0f", player.getMaxHealth()));
        playerLore.add("§7Kill Streak: §c" + rpgData.getKillStreak());
        playerLore.add("§7Total XP: §b" + skillData.getTotalXP());
        playerLore.add("§7Mobs Killed: §c" + stats.getTotalMobKills());
        playerLore.add("§7Enchantments Found: §5" + stats.getEnchantmentsFound());
        playerMeta.setLore(playerLore);
        playerInfo.setItemMeta(playerMeta);
        inventory.setItem(4, playerInfo);
        
        // Character creation section
        if (!rpgData.hasChosenRace()) {
            ItemStack raceSelect = new ItemStack(Material.BEACON);
            ItemMeta raceMeta = raceSelect.getItemMeta();
            raceMeta.setDisplayName("§6§lChoose Your Race §a(WORKING)");
            List<String> raceLore = new ArrayList<>();
            raceLore.add("§7Choose which race you want to play as");
            raceLore.add("§7Each race has different advantages");
            raceLore.add("§7and bonuses to different skills.");
            raceLore.add("");
            raceLore.add("§e▶ Click to choose your race!");
            raceMeta.setLore(raceLore);
            raceSelect.setItemMeta(raceMeta);
            inventory.setItem(10, raceSelect);
        }
        
        // Class selection (available after race)
        ItemStack classSelect = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta classMeta = classSelect.getItemMeta();
        if (!rpgData.hasChosenClass()) {
            classMeta.setDisplayName("§6§lChoose Your Class §a(WORKING)");
            List<String> classLore = new ArrayList<>();
            classLore.add("§7Classes provide unique bonuses");
            classLore.add("§7and abilities to enhance your");
            classLore.add("§7RPG experience.");
            classLore.add("");
            if (!rpgData.hasChosenRace()) {
                classLore.add("§c§lChoose your race first!");
            } else {
                classLore.add("§e▶ Click to choose your class!");
            }
            classMeta.setLore(classLore);
        } else {
            classMeta.setDisplayName("§6§lYour Class: " + rpgData.getPlayerClass().getDisplayName());
            List<String> classLore = new ArrayList<>();
            classLore.add("§7You are currently a §6" + rpgData.getPlayerClass().getDisplayName());
            classLore.add("");
            classLore.add("§e§lClass Bonuses:");
            for (String bonus : rpgData.getPlayerClass().getBonuses()) {
                classLore.add(bonus);
            }
            classMeta.setLore(classLore);
        }
        classSelect.setItemMeta(classMeta);
        inventory.setItem(13, classSelect);
        
        // Quests section
        ItemStack quests = new ItemStack(Material.MAP);
        ItemMeta questsMeta = quests.getItemMeta();
        questsMeta.setDisplayName("§e§lQuests & Adventures §c(EXPERIMENTAL)");
        List<String> questsLore = new ArrayList<>();
        questsLore.add("§7Complete quests to earn rewards");
        questsLore.add("§7Active Quests: §e" + rpgData.getActiveQuests().size());
        questsLore.add("§7Completed Quests: §a" + rpgData.getCompletedQuests().size());
        questsLore.add("");
        questsLore.add("§e▶ Click to view quests");
        questsMeta.setLore(questsLore);
        quests.setItemMeta(questsMeta);
        inventory.setItem(19, quests);
        
        // Enchantments section
        ItemStack enchants = new ItemStack(Material.ENCHANTING_TABLE);
        ItemMeta enchantsMeta = enchants.getItemMeta();
        enchantsMeta.setDisplayName("§5§lEnchants §c(EXPERIMENTAL)");
        List<String> enchantsLore = new ArrayList<>();
        enchantsLore.add("§7Manage your unlocked enchantments");
        enchantsLore.add("§7Apply enchants to your weapons");
        enchantsLore.add("§7Discover new enchantments");
        enchantsLore.add("");
        enchantsLore.add("§e▶ Click to enter Enchant Forge");
        enchantsMeta.setLore(enchantsLore);
        enchants.setItemMeta(enchantsMeta);
        inventory.setItem(21, enchants);
        
        // Skills section
        ItemStack skills = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta skillsMeta = skills.getItemMeta();
        skillsMeta.setDisplayName("§6§lSkills & Abilities §a(WORKING)");
        List<String> skillsLore = new ArrayList<>();
        skillsLore.add("§7Upgrade your combat skills");
        skillsLore.add("§7Increase damage and abilities");
        skillsLore.add("§7Spend your skill points wisely");
        skillsLore.add("");
        skillsLore.add("§e▶ Click to view skills");
        skillsMeta.setLore(skillsLore);
        skills.setItemMeta(skillsMeta);
        inventory.setItem(23, skills);
        
        // Shop section
        ItemStack shop = new ItemStack(Material.EMERALD);
        ItemMeta shopMeta = shop.getItemMeta();
        shopMeta.setDisplayName("§a§lShop §c(EXPERIMENTAL)");
        List<String> shopLore = new ArrayList<>();
        shopLore.add("§7Buy and sell weapons, armor");
        shopLore.add("§7and crafting materials");
        shopLore.add("§7Your septims: §6" + rpgData.getCoins());
        shopLore.add("");
        shopLore.add("§e▶ Click to open shop");
        shopMeta.setLore(shopLore);
        shop.setItemMeta(shopMeta);
        inventory.setItem(25, shop);
        
        // Crafting section
        ItemStack crafting = new ItemStack(Material.ANVIL);
        ItemMeta craftingMeta = crafting.getItemMeta();
        craftingMeta.setDisplayName("§6§lSkyforge §a(WORKING)");
        List<String> craftingLore = new ArrayList<>();
        craftingLore.add("§7Forge weapons and armor");
        craftingLore.add("§7Create powerful equipment");
        int smithingLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(sonemc.soneRPG.enums.SkillType.SMITHING);
        craftingLore.add("§7Smithing Level: §6" + smithingLevel);
        craftingLore.add("");
        craftingLore.add("§e▶ Click to enter smithy");
        craftingMeta.setLore(craftingLore);
        crafting.setItemMeta(craftingMeta);
        inventory.setItem(28, crafting);
        
        // Alchemy section
        ItemStack alchemy = new ItemStack(Material.BREWING_STAND);
        ItemMeta alchemyMeta = alchemy.getItemMeta();
        alchemyMeta.setDisplayName("§2§lAlchemy Laboratory §c(EXPERIMENTAL)");
        List<String> alchemyLore = new ArrayList<>();
        alchemyLore.add("§7Brew potions and poisons");
        alchemyLore.add("§7Create healing elixirs");
        alchemyLore.add("§7Craft weapon coatings");
        int alchemyLevel = plugin.getSkillManager().getPlayerData(player).getSkillLevel(sonemc.soneRPG.enums.SkillType.ALCHEMY);
        alchemyLore.add("§7Alchemy Level: §2" + alchemyLevel);
        alchemyLore.add("");
        alchemyLore.add("§e▶ Click to enter laboratory");
        alchemyMeta.setLore(alchemyLore);
        alchemy.setItemMeta(alchemyMeta);
        inventory.setItem(30, alchemy);
        
        // Statistics section
        ItemStack statistics = new ItemStack(Material.BOOK);
        ItemMeta statsMeta = statistics.getItemMeta();
        statsMeta.setDisplayName("§3§lCombat Statistics §a(WORKING)");
        List<String> statsLore = new ArrayList<>();
        statsLore.add("§7View your combat statistics");
        statsLore.add("§7Track your progress");
        statsLore.add("§7See detailed kill counts");
        statsLore.add("");
        statsLore.add("§e▶ Click to view statistics");
        statsMeta.setLore(statsLore);
        statistics.setItemMeta(statsMeta);
        inventory.setItem(32, statistics);
        
        // Leaderboard section
        ItemStack leaderboard = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta leaderMeta = leaderboard.getItemMeta();
        leaderMeta.setDisplayName("§e§lLeaderboards §a(WORKING)");
        List<String> leaderLore = new ArrayList<>();
        leaderLore.add("§7See top players on the server");
        leaderLore.add("§7Compare your progress");
        leaderLore.add("§7Compete for the top spot!");
        leaderLore.add("");
        leaderLore.add("§e▶ Click to view leaderboards");
        leaderMeta.setLore(leaderLore);
        leaderboard.setItemMeta(leaderMeta);
        inventory.setItem(34, leaderboard);
        
        // Fill empty slots with glass panes
        ItemStack filler = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    public void open() {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player clicker = (Player) event.getWhoClicked();
        if (!clicker.equals(player)) {
            return;
        }
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        switch (clickedItem.getType()) {
            case BEACON:
                // Open race selection
                player.closeInventory();
                RaceSelectionGUI raceGUI = new RaceSelectionGUI(plugin, player);
                raceGUI.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.2f);
                break;
                
            case DIAMOND_SWORD:
                // Open class selection
                if (!plugin.getRPGDataManager().getPlayerRPGData(player).hasChosenRace()) {
                    player.sendMessage("§cYou must choose your race first!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return;
                }
                
                if (plugin.getRPGDataManager().getPlayerRPGData(player).hasChosenClass()) {
                    player.sendMessage("§cYou have already chosen your class!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return;
                }
                
                player.closeInventory();
                ClassSelectionGUI classGUI = new ClassSelectionGUI(plugin, player);
                classGUI.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.2f);
                break;
                
            case MAP:
                // Open quests GUI
                player.closeInventory();
                QuestGUI questGUI = new QuestGUI(plugin, player);
                questGUI.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.2f);
                break;
                
            case ENCHANTING_TABLE:
                // Open enchant forge
                player.closeInventory();
                EnchantForgeGUI enchantGUI = new EnchantForgeGUI(plugin, player);
                enchantGUI.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.2f);
                break;
                
            case EXPERIENCE_BOTTLE:
                // Open skills GUI
                player.closeInventory();
                SkillsGUI skillsGUI = new SkillsGUI(plugin, player);
                skillsGUI.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
                break;
                
            case EMERALD:
                // Open shop
                player.closeInventory();
                ShopGUI shopGUI = new ShopGUI(plugin, player, "weapons");
                shopGUI.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_CHEST_OPEN, 1.0f, 1.2f);
                break;
                
            case ANVIL:
                // Open crafting
                player.closeInventory();
                CraftingGUI craftingGUI = new CraftingGUI(plugin, player);
                craftingGUI.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
                break;
                
            case BREWING_STAND:
                // Open alchemy
                player.closeInventory();
                AlchemyGUI alchemyGUI = new AlchemyGUI(plugin, player);
                alchemyGUI.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.2f);
                break;
                
            case BOOK:
                // Open statistics GUI
                player.closeInventory();
                StatisticsGUI statsGUI = new StatisticsGUI(plugin, player);
                statsGUI.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
                break;
                
            case GOLDEN_SWORD:
                // Open leaderboard GUI
                player.closeInventory();
                LeaderboardGUI leaderGUI = new LeaderboardGUI(plugin, player);
                leaderGUI.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                break;
        }
    }
}