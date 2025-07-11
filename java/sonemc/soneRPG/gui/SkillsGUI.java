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
import sonemc.soneRPG.enums.SkillType;

import java.util.ArrayList;
import java.util.List;

public class SkillsGUI implements Listener {

    private final SoneRPG plugin;
    private final Player player;
    private final Inventory inventory;

    public SkillsGUI(SoneRPG plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, "§6§lSkills & Upgrades");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        PlayerSkillData data = plugin.getSkillManager().getPlayerData(player);

        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to RPG Hub");
        backButton.setItemMeta(backMeta);
        inventory.setItem(0, backButton);

        ItemStack playerInfo = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta playerMeta = playerInfo.getItemMeta();
        playerMeta.setDisplayName("§a§l" + player.getName() + "'s Skills");
        List<String> playerLore = new ArrayList<>();
        playerLore.add("§7RPG Level: §a" + data.getRPGLevel() + "§7/§a20");
        playerLore.add("§7Skill Points: §e" + data.getSkillPoints());
        playerLore.add("§7Total XP: §b" + data.getTotalXP());
        playerLore.add("");
        playerLore.add("§7Click skills below to upgrade them!");
        playerMeta.setLore(playerLore);
        playerInfo.setItemMeta(playerMeta);
        inventory.setItem(4, playerInfo);

        int[] skillSlots = {19, 21, 23, 25, 28, 30, 32, 34, 37};
        SkillType[] skills = SkillType.values();

        for (int i = 0; i < skills.length && i < skillSlots.length; i++) {
            SkillType skill = skills[i];
            ItemStack skillItem = createSkillItem(skill, data);
            inventory.setItem(skillSlots[i], skillItem);
        }

        ItemStack filler = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private ItemStack createSkillItem(SkillType skillType, PlayerSkillData data) {
        ItemStack item = new ItemStack(skillType.getIcon());
        ItemMeta meta = item.getItemMeta();

        int currentLevel = data.getSkillLevel(skillType);
        int maxLevel = 10;
        boolean canUpgrade = data.getSkillPoints() > 0 && currentLevel < maxLevel;

        String color = canUpgrade ? "§a" : (currentLevel > 0 ? "§e" : "§7");
        meta.setDisplayName(color + "§l" + skillType.getDisplayName());

        List<String> lore = new ArrayList<>();

        String[] descLines = skillType.getDescription().split("\n");
        for (String line : descLines) {
            lore.add("§7" + line);
        }

        lore.add("");
        lore.add("§7Current Level: §f" + currentLevel + "§7/§f" + maxLevel);

        if (currentLevel > 0) {
            addSkillBonusLore(lore, skillType, currentLevel);
        }

        if (currentLevel < maxLevel) {
            lore.add("");
            lore.add("§6Next Level Bonuses:");
            addSkillBonusLore(lore, skillType, currentLevel + 1);
        }

        lore.add("");
        lore.add("§7XP Earned: §b" + data.getSkillXP(skillType));
        lore.add("");

        if (canUpgrade) {
            lore.add("§a§l▶ Click to upgrade! (1 Skill Point)");
            item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } else if (currentLevel >= maxLevel) {
            lore.add("§6§lMAX LEVEL REACHED");
        } else {
            lore.add("§c§lNo skill points available");
        }

        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private void addSkillBonusLore(List<String> lore, SkillType skillType, int level) {
        switch (skillType) {
            case SWORD_DAMAGE:
                lore.add("§a  +" + String.format("%.1f", level * 5.0) + "% Sword Damage");
                break;
            case BOW_DAMAGE:
                lore.add("§a  +" + String.format("%.1f", level * 4.0) + "% Bow Damage");
                break;
            case LIGHT_ARMOR_SPEED:
                lore.add("§b  +" + String.format("%.1f", level * 3.0) + "% Movement Speed");
                break;
            case ALCHEMY:
                lore.add("§2  +" + String.format("%.1f", level * 5.0) + "% Potion Effectiveness");
                break;
            case RESTORATION:
                lore.add("§a  +" + String.format("%.1f", level * 1.0) + "% Golden Apple Healing");
                break;
            case SMITHING:
                lore.add("§7  Unlocks better crafting recipes");
                break;
            case ENCHANTING:
                lore.add("§5  +" + String.format("%.1f", level * 2.0) + "% Enchantment Effectiveness");
                break;
            case HEAVY_ARMOR:
                lore.add("§7  +" + String.format("%.1f", level * 2.0) + "% Damage Reduction");
                break;
            case STAMINA:
                lore.add("§e  +" + (level * 15) + " Max Stamina");
                lore.add("§b  +" + String.format("%.1f", level * 5.0) + "% Sprint Efficiency");
                break;
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

        if (clickedItem.getType() == Material.ARROW) {
            player.closeInventory();
            RPGMainGUI mainGUI = new RPGMainGUI(plugin, player);
            mainGUI.open();
            return;
        }

        for (SkillType skillType : SkillType.values()) {
            if (clickedItem.getType() == skillType.getIcon()) {
                handleSkillClick(skillType);
                break;
            }
        }
    }

    private void handleSkillClick(SkillType skillType) {
        PlayerSkillData data = plugin.getSkillManager().getPlayerData(player);

        if (data.getSkillPoints() <= 0) {
            player.sendMessage("§cYou don't have any skill points to spend!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        if (data.getSkillLevel(skillType) >= 10) {
            player.sendMessage("§cThis skill is already at maximum level!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        if (plugin.getSkillManager().upgradeSkill(player, skillType)) {
            int newLevel = data.getSkillLevel(skillType);
            player.sendMessage("§a§l✦ Skill Upgraded! §7" + skillType.getDisplayName() + " is now level " + newLevel);

            String bonusMessage = getUpgradeBonusMessage(skillType, newLevel);
            if (!bonusMessage.isEmpty()) {
                player.sendMessage(bonusMessage);
            }

            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);

            inventory.clear();
            setupGUI();
        }
    }

    private String getUpgradeBonusMessage(SkillType skillType, int newLevel) {
        switch (skillType) {
            case SWORD_DAMAGE:
                return "§7New bonus: §a+" + String.format("%.1f", newLevel * 5.0) + "% sword damage";
            case BOW_DAMAGE:
                return "§7New bonus: §a+" + String.format("%.1f", newLevel * 4.0) + "% bow damage";
            case LIGHT_ARMOR_SPEED:
                return "§7New bonus: §b+" + String.format("%.1f", newLevel * 3.0) + "% movement speed";
            case STAMINA:
                return "§7New bonus: §e+" + (newLevel * 15) + " max stamina";
            default:
                return "";
        }
    }
}