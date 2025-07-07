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
import sonemc.soneRPG.data.Quest;
import sonemc.soneRPG.data.PlayerRPGData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public QuestGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, "§e§lQuests & Adventures");
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to RPG Hub");
        backButton.setItemMeta(backMeta);
        inventory.setItem(0, backButton);

        // Quest info
        ItemStack questInfo = new ItemStack(Material.MAP);
        ItemMeta questMeta = questInfo.getItemMeta();
        questMeta.setDisplayName("§e§lYour Quests");
        List<String> questLore = new ArrayList<>();
        questLore.add("§7Active Quests: §e" + rpgData.getActiveQuests().size());
        questLore.add("§7Completed Quests: §a" + rpgData.getCompletedQuests().size());
        questLore.add("");
        questLore.add("§7Complete quests to earn rewards!");
        questMeta.setLore(questLore);
        questInfo.setItemMeta(questMeta);
        inventory.setItem(4, questInfo);

        // Display active quests
        int slot = 9;
        for (Quest quest : rpgData.getActiveQuests().values()) {
            if (slot >= 45) break;
            ItemStack questItem = createQuestItem(quest, false);
            inventory.setItem(slot, questItem);
            slot++;
        }

        // Display available quests
        List<Quest> availableQuests = plugin.getQuestManager().getAvailableQuests(player);
        for (Quest quest : availableQuests) {
            if (slot >= 45) break;
            if (!rpgData.getActiveQuests().containsKey(quest.getId()) && 
                !rpgData.getCompletedQuests().containsKey(quest.getId())) {
                ItemStack questItem = createQuestItem(quest, true);
                inventory.setItem(slot, questItem);
                slot++;
            }
        }

        // Fill empty slots
        ItemStack filler = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private ItemStack createQuestItem(Quest quest, boolean isAvailable) {
        ItemStack item = new ItemStack(quest.getIcon());
        ItemMeta meta = item.getItemMeta();

        String color = quest.isCompleted() ? "§a" : isAvailable ? "§e" : "§7";
        meta.setDisplayName(color + "§l" + quest.getName());

        List<String> lore = new ArrayList<>();
        lore.add("§7" + quest.getDescription());
        lore.add("");
        lore.add("§7Type: §f" + quest.getType().getDisplayName());
        lore.add("§7Required Level: §f" + quest.getRequiredLevel());
        lore.add("");

        if (quest.isCompleted()) {
            lore.add("§a§l✓ COMPLETED");
            if (!quest.isClaimed()) {
                lore.add("§e▶ Click to claim rewards!");
            } else {
                lore.add("§7Rewards already claimed");
            }
        } else if (isAvailable) {
            lore.add("§e§l▶ AVAILABLE");
            lore.add("§7Click to accept this quest!");
        } else {
            lore.add("§7Progress: §f" + quest.getProgress() + "§7/§f" + quest.getTargetAmount());
            lore.add("§7Completion: §f" + String.format("%.1f", quest.getProgressPercentage()) + "%");
        }

        lore.add("");
        lore.add("§6§lRewards:");
        for (Quest.QuestReward reward : quest.getRewards()) {
            lore.add("§7• " + formatReward(reward));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private String formatReward(Quest.QuestReward reward) {
        switch (reward.getType()) {
            case XP:
                return "§b" + reward.getAmount() + " XP";
            case SKILL_POINTS:
                return "§e" + reward.getAmount() + " Skill Points";
            case ENCHANTMENT:
                return "§5" + reward.getData() + " Enchantment";
            case ITEM:
                return "§f" + reward.getAmount() + "x " + reward.getData();
            case COINS:
                return "§6" + reward.getAmount() + " Coins";
            default:
                return "§7Unknown Reward";
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

        // Back button
        if (clickedItem.getType() == Material.ARROW) {
            player.closeInventory();
            RPGMainGUI mainGUI = new RPGMainGUI(plugin, player);
            mainGUI.open();
            return;
        }

        // Handle quest interactions
        handleQuestClick(clickedItem);
    }

    private void handleQuestClick(ItemStack questItem) {
        String questName = questItem.getItemMeta().getDisplayName();
        questName = questName.replaceAll("§[0-9a-fk-or]", ""); // Remove color codes
        
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        // Find the quest
        Quest quest = null;
        for (Quest q : rpgData.getActiveQuests().values()) {
            if (q.getName().equals(questName)) {
                quest = q;
                break;
            }
        }
        
        if (quest == null) {
            // Check available quests
            for (Quest q : plugin.getQuestManager().getAvailableQuests(player)) {
                if (q.getName().equals(questName)) {
                    quest = q;
                    break;
                }
            }
        }
        
        if (quest != null) {
            if (quest.isCompleted() && !quest.isClaimed()) {
                // Claim rewards
                plugin.getQuestManager().claimQuestRewards(player, quest);
                player.closeInventory();
                QuestGUI newGUI = new QuestGUI(plugin, player);
                newGUI.open();
            } else if (!rpgData.getActiveQuests().containsKey(quest.getId()) && 
                      !rpgData.getCompletedQuests().containsKey(quest.getId())) {
                // Accept quest
                plugin.getQuestManager().acceptQuest(player, quest);
                player.closeInventory();
                QuestGUI newGUI = new QuestGUI(plugin, player);
                newGUI.open();
            }
        }
    }
}