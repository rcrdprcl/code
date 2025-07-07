package sonemc.soneRPG.managers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.CustomEnchantment;
import sonemc.soneRPG.utils.ActionBarUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EnchantmentManager {

    private final SoneRPG plugin;
    private final Map<UUID, Set<CustomEnchantment>> unlockedEnchantments;
    private final Map<UUID, Map<CustomEnchantment, Integer>> availableEnchantments;
    private final NamespacedKey enchantmentKey;
    private File enchantmentsFile;
    private FileConfiguration enchantmentsConfig;
    private final Random random;

    public EnchantmentManager(SoneRPG plugin) {
        this.plugin = plugin;
        this.unlockedEnchantments = new HashMap<>();
        this.availableEnchantments = new HashMap<>();
        this.enchantmentKey = new NamespacedKey(plugin, "custom_enchantment");
        this.random = new Random();
    }

    public void createEnchantmentsConfig() {
        enchantmentsFile = new File(plugin.getDataFolder(), "enchantments.yml");
        if (!enchantmentsFile.exists()) {
            try {
                enchantmentsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create enchantments.yml: " + e.getMessage());
            }
        }
        enchantmentsConfig = YamlConfiguration.loadConfiguration(enchantmentsFile);
        loadPlayerData();
    }

    public Set<CustomEnchantment> getUnlockedEnchantments(Player player) {
        return unlockedEnchantments.getOrDefault(player.getUniqueId(), new HashSet<>());
    }

    public boolean hasAvailableEnchantment(Player player, CustomEnchantment enchantment) {
        Map<CustomEnchantment, Integer> playerEnchants = availableEnchantments.get(player.getUniqueId());
        if (playerEnchants == null) return false;
        return playerEnchants.getOrDefault(enchantment, 0) > 0;
    }

    public int getAvailableCount(Player player, CustomEnchantment enchantment) {
        Map<CustomEnchantment, Integer> playerEnchants = availableEnchantments.get(player.getUniqueId());
        if (playerEnchants == null) return 0;
        return playerEnchants.getOrDefault(enchantment, 0);
    }

    public void addEnchantment(Player player, CustomEnchantment enchantment) {
        // Unlock the enchantment type
        Set<CustomEnchantment> playerUnlocked = unlockedEnchantments.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        boolean wasNewUnlock = !playerUnlocked.contains(enchantment);
        playerUnlocked.add(enchantment);
        
        // Add to available enchantments
        Map<CustomEnchantment, Integer> playerAvailable = availableEnchantments.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        playerAvailable.put(enchantment, playerAvailable.getOrDefault(enchantment, 0) + 1);
        
        savePlayerData(player);
        
        if (wasNewUnlock) {
            // Use action bar for enchantment discovery
            ActionBarUtils.sendEnchantmentFound(player, enchantment.getDisplayName());
            
            player.sendMessage("§a§l✦ Enchantment Discovered! §7You have unlocked §5" + enchantment.getDisplayName() + "§7!");
            player.sendMessage("§7Use §e/rpgui §7→ §5Enchants §7to apply it to your weapons.");
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.2f);
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
        } else {
            // Use action bar for additional copies
            ActionBarUtils.sendEnchantmentFound(player, enchantment.getDisplayName() + " x" + getAvailableCount(player, enchantment));
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.2f);
        }
    }

    public void consumeEnchantment(Player player, CustomEnchantment enchantment) {
        Map<CustomEnchantment, Integer> playerAvailable = availableEnchantments.get(player.getUniqueId());
        if (playerAvailable != null) {
            int current = playerAvailable.getOrDefault(enchantment, 0);
            if (current > 0) {
                playerAvailable.put(enchantment, current - 1);
                savePlayerData(player);
            }
        }
    }

    public boolean hasUnlockedEnchantment(Player player, CustomEnchantment enchantment) {
        return getUnlockedEnchantments(player).contains(enchantment);
    }

    public ItemStack applyCustomEnchantment(ItemStack item, CustomEnchantment enchantment) {
        if (item == null || item.getType() == Material.AIR) {
            return item;
        }
        
        if (!enchantment.canApplyTo(item.getType())) {
            return item;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        
        // Store enchantment in persistent data
        meta.getPersistentDataContainer().set(enchantmentKey, PersistentDataType.STRING, enchantment.name());
        
        // Update lore
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        
        // Remove existing custom enchantment lore
        lore.removeIf(line -> line.contains("§5[Forged]"));
        
        // Add new enchantment lore
        lore.add("§5[Forged] " + enchantment.getDisplayName());
        lore.add("§8" + enchantment.getDescription().replace("§c", "§7").replace("§a", "§7").replace("§e", "§7").replace("§b", "§7").replace("§d", "§7").replace("§6", "§7").replace("§5", "§7").replace("§4", "§7"));
        
        meta.setLore(lore);
        
        // Hide attributes to remove "When in Main hand" text
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        
        item.setItemMeta(meta);
        
        return item;
    }

    public CustomEnchantment getCustomEnchantment(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        String enchantName = meta.getPersistentDataContainer().get(enchantmentKey, PersistentDataType.STRING);
        if (enchantName == null) {
            return null;
        }
        
        try {
            return CustomEnchantment.valueOf(enchantName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public ItemStack removeCustomEnchantment(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return item;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        
        // Remove persistent data
        meta.getPersistentDataContainer().remove(enchantmentKey);
        
        // Remove custom enchantment lore
        List<String> lore = meta.getLore();
        if (lore != null) {
            lore.removeIf(line -> line.contains("§5[Forged]") || (line.contains("§8") && line.length() > 10));
            if (lore.isEmpty()) {
                lore = null;
            }
            meta.setLore(lore);
        }
        
        // Remove attribute hiding
        meta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        
        item.setItemMeta(meta);
        return item;
    }

    public boolean tryUnlockEnchantment(Player player, int mobLevel) {
        for (CustomEnchantment enchant : CustomEnchantment.values()) {
            // Much harder unlock chance - significantly increased difficulty
            int baseChance = enchant.getDropChance() * 8; // Increased from 4 to 8 for much harder unlocking
            int adjustedChance = Math.max(baseChance, baseChance - (mobLevel * 1)); // Reduced mob level bonus
            
            // Additional difficulty scaling based on enchantment rarity
            if (enchant.name().contains("LEGENDARY") || enchant.name().contains("SOUL_TRAP") ||
                    enchant.name().contains("LIGHTNING_STRIKE") || enchant.name().contains("VAMPIRE")) {
                adjustedChance *= 3; // Triple difficulty for rare enchants
            }
            
            if (random.nextInt(adjustedChance) == 0) {
                addEnchantment(player, enchant);
                return true;
            }
        }
        
        return false;
    }

    private void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid.toString();
        
        // Save unlocked enchantments
        Set<CustomEnchantment> unlocked = unlockedEnchantments.get(uuid);
        if (unlocked != null && !unlocked.isEmpty()) {
            List<String> enchantNames = new ArrayList<>();
            for (CustomEnchantment enchant : unlocked) {
                enchantNames.add(enchant.name());
            }
            enchantmentsConfig.set(path + ".unlocked", enchantNames);
        }
        
        // Save available enchantments
        Map<CustomEnchantment, Integer> available = availableEnchantments.get(uuid);
        if (available != null && !available.isEmpty()) {
            for (Map.Entry<CustomEnchantment, Integer> entry : available.entrySet()) {
                if (entry.getValue() > 0) {
                    enchantmentsConfig.set(path + ".available." + entry.getKey().name(), entry.getValue());
                }
            }
        }
        
        saveEnchantmentsConfig();
    }

    private void loadPlayerData() {
        if (enchantmentsConfig.getConfigurationSection("players") == null) {
            return;
        }
        
        for (String uuidString : enchantmentsConfig.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(uuidString);
                String path = "players." + uuidString;
                
                // Load unlocked enchantments
                List<String> unlockedNames = enchantmentsConfig.getStringList(path + ".unlocked");
                Set<CustomEnchantment> unlocked = new HashSet<>();
                for (String enchantName : unlockedNames) {
                    try {
                        unlocked.add(CustomEnchantment.valueOf(enchantName));
                    } catch (IllegalArgumentException e) {
                        // Skip invalid enchantment names
                    }
                }
                unlockedEnchantments.put(playerUUID, unlocked);
                
                // Load available enchantments
                if (enchantmentsConfig.getConfigurationSection(path + ".available") != null) {
                    Map<CustomEnchantment, Integer> available = new HashMap<>();
                    for (String enchantName : enchantmentsConfig.getConfigurationSection(path + ".available").getKeys(false)) {
                        try {
                            CustomEnchantment enchant = CustomEnchantment.valueOf(enchantName);
                            int count = enchantmentsConfig.getInt(path + ".available." + enchantName);
                            available.put(enchant, count);
                        } catch (IllegalArgumentException e) {
                            // Skip invalid enchantment names
                        }
                    }
                    availableEnchantments.put(playerUUID, available);
                }
                
            } catch (IllegalArgumentException e) {
                // Skip invalid UUIDs
            }
        }
    }

    public void saveAllPlayerData() {
        for (UUID playerUUID : unlockedEnchantments.keySet()) {
            String path = "players." + playerUUID.toString();
            
            // Save unlocked enchantments
            Set<CustomEnchantment> unlocked = unlockedEnchantments.get(playerUUID);
            if (unlocked != null && !unlocked.isEmpty()) {
                List<String> enchantNames = new ArrayList<>();
                for (CustomEnchantment enchant : unlocked) {
                    enchantNames.add(enchant.name());
                }
                enchantmentsConfig.set(path + ".unlocked", enchantNames);
            }
            
            // Save available enchantments
            Map<CustomEnchantment, Integer> available = availableEnchantments.get(playerUUID);
            if (available != null && !available.isEmpty()) {
                for (Map.Entry<CustomEnchantment, Integer> entry : available.entrySet()) {
                    if (entry.getValue() > 0) {
                        enchantmentsConfig.set(path + ".available." + entry.getKey().name(), entry.getValue());
                    }
                }
            }
        }
        saveEnchantmentsConfig();
    }

    private void saveEnchantmentsConfig() {
        try {
            enchantmentsConfig.save(enchantmentsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save enchantments.yml: " + e.getMessage());
        }
    }
}