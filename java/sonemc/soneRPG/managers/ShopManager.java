package sonemc.soneRPG.managers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.data.PlayerRPGData;
import sonemc.soneRPG.enums.ItemRarity;
import sonemc.soneRPG.enums.PlayerRace;
import sonemc.soneRPG.enums.CustomEnchantment;

import java.util.*;

public class ShopManager {
    
    private final SoneRPG plugin;
    private final Map<String, ShopItem> shopItems;
    private final Random random;
    
    public ShopManager(SoneRPG plugin) {
        this.plugin = plugin;
        this.shopItems = new HashMap<>();
        this.random = new Random();
        initializeShopItems();
    }
    
    private void initializeShopItems() {
        // Basic Weapons
        addShopItem("iron_sword", Material.IRON_SWORD, "Iron Sword", 200, 100, ItemRarity.COMMON, "A sturdy iron blade for combat");
        addShopItem("diamond_sword", Material.DIAMOND_SWORD, "Diamond Sword", 1000, 500, ItemRarity.RARE, "A pristine diamond blade of exceptional quality");
        addShopItem("netherite_sword", Material.NETHERITE_SWORD, "Netherite Sword", 2500, 1250, ItemRarity.EPIC, "Ancient netherite forged in hellfire");
        
        addShopItem("iron_axe", Material.IRON_AXE, "Iron Battleaxe", 220, 110, ItemRarity.COMMON, "Heavy iron axe for devastating attacks");
        addShopItem("diamond_axe", Material.DIAMOND_AXE, "Diamond Battleaxe", 1100, 550, ItemRarity.RARE, "Razor-sharp diamond axe");
        addShopItem("netherite_axe", Material.NETHERITE_AXE, "Netherite Battleaxe", 2700, 1350, ItemRarity.EPIC, "Legendary netherite battleaxe");
        
        addShopItem("bow", Material.BOW, "Hunter's Bow", 150, 75, ItemRarity.COMMON, "Reliable bow for hunting and combat");
        addShopItem("crossbow", Material.CROSSBOW, "Heavy Crossbow", 300, 150, ItemRarity.UNCOMMON, "Powerful crossbow with precise aim");

        // Enchanted Weapons
        addEnchantedWeapon("enchanted_iron_sword_rage", Material.IRON_SWORD, "Sword of Rage", 800, 400, ItemRarity.UNCOMMON, "Iron sword imbued with berserker fury", CustomEnchantment.RAGE);
        addEnchantedWeapon("enchanted_bow_sharp_eye", Material.BOW, "Eagle Eye Bow", 1200, 600, ItemRarity.RARE, "Bow blessed with supernatural accuracy", CustomEnchantment.SHARP_EYE);
        addEnchantedWeapon("enchanted_axe_frost", Material.DIAMOND_AXE, "Frostbite Axe", 2200, 1100, ItemRarity.EPIC, "Diamond axe infused with eternal frost", CustomEnchantment.FROST_BITE);
        addEnchantedWeapon("enchanted_sword_berserker", Material.DIAMOND_SWORD, "Berserker Blade", 3000, 1500, ItemRarity.LEGENDARY, "Legendary blade that grows stronger in battle", CustomEnchantment.BERSERKER);
        addEnchantedWeapon("enchanted_sword_fire", Material.NETHERITE_SWORD, "Flame Reaper", 4000, 2000, ItemRarity.LEGENDARY, "Netherite blade that burns enemies", CustomEnchantment.FIRE_DAMAGE);
        
        // Basic Armor
        addShopItem("iron_helmet", Material.IRON_HELMET, "Iron Helmet", 120, 60, ItemRarity.COMMON, "Protective iron headgear");
        addShopItem("iron_chestplate", Material.IRON_CHESTPLATE, "Iron Armor", 250, 125, ItemRarity.COMMON, "Sturdy iron chest protection");
        addShopItem("iron_leggings", Material.IRON_LEGGINGS, "Iron Greaves", 180, 90, ItemRarity.COMMON, "Iron leg armor");
        addShopItem("iron_boots", Material.IRON_BOOTS, "Iron Boots", 100, 50, ItemRarity.COMMON, "Heavy iron boots");
        
        addShopItem("diamond_helmet", Material.DIAMOND_HELMET, "Diamond Helmet", 800, 400, ItemRarity.RARE, "Gleaming diamond helmet");
        addShopItem("diamond_chestplate", Material.DIAMOND_CHESTPLATE, "Diamond Armor", 1500, 750, ItemRarity.RARE, "Magnificent diamond chestplate");
        addShopItem("diamond_leggings", Material.DIAMOND_LEGGINGS, "Diamond Greaves", 1200, 600, ItemRarity.RARE, "Brilliant diamond leggings");
        addShopItem("diamond_boots", Material.DIAMOND_BOOTS, "Diamond Boots", 600, 300, ItemRarity.RARE, "Radiant diamond boots");

        addShopItem("netherite_helmet", Material.NETHERITE_HELMET, "Netherite Helmet", 2000, 1000, ItemRarity.EPIC, "Indestructible netherite helmet");
        addShopItem("netherite_chestplate", Material.NETHERITE_CHESTPLATE, "Netherite Armor", 3500, 1750, ItemRarity.EPIC, "Ultimate netherite protection");
        addShopItem("netherite_leggings", Material.NETHERITE_LEGGINGS, "Netherite Greaves", 2800, 1400, ItemRarity.EPIC, "Legendary netherite leg armor");
        addShopItem("netherite_boots", Material.NETHERITE_BOOTS, "Netherite Boots", 1500, 750, ItemRarity.EPIC, "Unbreakable netherite boots");

        // Enchanted Armor
        addEnchantedArmor("enchanted_iron_chestplate_health", Material.IRON_CHESTPLATE, "Chestplate of Vitality", 1000, 500, ItemRarity.UNCOMMON, "Iron armor that enhances life force", CustomEnchantment.FORTIFY_HEALTH);
        addEnchantedArmor("enchanted_diamond_boots_speed", Material.DIAMOND_BOOTS, "Boots of Swiftness", 1800, 900, ItemRarity.RARE, "Diamond boots that grant incredible speed", CustomEnchantment.FORTIFY_SPEED);
        addEnchantedArmor("enchanted_netherite_chestplate_fire_resist", Material.NETHERITE_CHESTPLATE, "Dragonscale Armor", 5000, 2500, ItemRarity.LEGENDARY, "Netherite armor immune to fire", CustomEnchantment.FIRE_RESISTANCE);
        addEnchantedArmor("enchanted_diamond_helmet_waterbreathing", Material.DIAMOND_HELMET, "Aquatic Helm", 2000, 1000, ItemRarity.RARE, "Helmet that grants underwater breathing", CustomEnchantment.WATERBREATHING);
        addEnchantedArmor("enchanted_diamond_chestplate_regen", Material.DIAMOND_CHESTPLATE, "Regenerating Armor", 2500, 1250, ItemRarity.EPIC, "Armor that slowly heals the wearer", CustomEnchantment.REGENERATION);
        
        // Consumables
        addShopItem("health_potion", Material.POTION, "Health Potion", 75, 35, ItemRarity.COMMON, "Restores health when consumed");
        addShopItem("strength_potion", Material.POTION, "Strength Potion", 150, 75, ItemRarity.UNCOMMON, "Temporarily increases damage");
        addShopItem("speed_potion", Material.POTION, "Speed Potion", 120, 60, ItemRarity.UNCOMMON, "Temporarily increases movement speed");
        
        // Crafting Materials
        addShopItem("iron_ingot", Material.IRON_INGOT, "Iron Ingot", 30, 15, ItemRarity.COMMON, "Pure iron for smithing");
        addShopItem("gold_ingot", Material.GOLD_INGOT, "Gold Ingot", 60, 30, ItemRarity.COMMON, "Valuable gold ingot");
        addShopItem("diamond", Material.DIAMOND, "Diamond", 250, 125, ItemRarity.UNCOMMON, "Precious diamond gemstone");
        addShopItem("emerald", Material.EMERALD, "Emerald", 200, 100, ItemRarity.UNCOMMON, "Magical emerald");
        addShopItem("netherite_ingot", Material.NETHERITE_INGOT, "Netherite Ingot", 1000, 500, ItemRarity.RARE, "Legendary netherite ingot");
        
        // Special Items
        addShopItem("dragon_scale", Material.PRISMARINE_SHARD, "Dragon Scale", 800, 400, ItemRarity.EPIC, "Scale from an ancient dragon");
        addShopItem("ancient_scroll", Material.PAPER, "Ancient Scroll", 1500, 750, ItemRarity.LEGENDARY, "Contains forgotten knowledge");
        addShopItem("enchanted_dust", Material.GLOWSTONE_DUST, "Enchanted Dust", 100, 50, ItemRarity.UNCOMMON, "Magical dust for enchanting");
        addShopItem("void_crystal", Material.END_CRYSTAL, "Void Crystal", 2000, 1000, ItemRarity.LEGENDARY, "Crystal from the void realm");
    }
    
    private void addShopItem(String id, Material material, String name, int buyPrice, int sellPrice, ItemRarity rarity, String description) {
        shopItems.put(id, new ShopItem(id, material, name, buyPrice, sellPrice, rarity, description, null));
    }

    private void addEnchantedWeapon(String id, Material material, String name, int buyPrice, int sellPrice, ItemRarity rarity, String description, CustomEnchantment enchantment) {
        shopItems.put(id, new ShopItem(id, material, name, buyPrice, sellPrice, rarity, description, enchantment));
    }

    private void addEnchantedArmor(String id, Material material, String name, int buyPrice, int sellPrice, ItemRarity rarity, String description, CustomEnchantment enchantment) {
        shopItems.put(id, new ShopItem(id, material, name, buyPrice, sellPrice, rarity, description, enchantment));
    }
    
    public boolean withdrawCoins(Player player, int amount) {
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        
        if (!rpgData.spendCoins(amount)) {
            return false;
        }
        
        // Give coins as physical items (gold ingots)
        int goldIngots = amount / 10; // 10 coins = 1 gold ingot
        if (goldIngots > 0) {
            ItemStack coins = new ItemStack(Material.GOLD_INGOT, goldIngots);
            ItemMeta meta = coins.getItemMeta();
            meta.setDisplayName("§6Septims");
            List<String> lore = new ArrayList<>();
            lore.add("§7Value: §6" + (goldIngots * 10) + " septims");
            lore.add("§7Currency of the realm");
            meta.setLore(lore);
            coins.setItemMeta(meta);
            
            player.getInventory().addItem(coins);
        }
        
        return true;
    }
    
    public boolean depositCoins(Player player, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.GOLD_INGOT) {
            return false;
        }
        
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || !meta.getDisplayName().equals("§6Septims")) {
            return false;
        }
        
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        int coinValue = itemStack.getAmount() * 10; // 1 gold ingot = 10 coins
        
        rpgData.addCoins(coinValue);
        player.getInventory().removeItem(itemStack);
        
        return true;
    }
    
    public boolean buyItem(Player player, String itemId, int quantity) {
        ShopItem item = shopItems.get(itemId);
        if (item == null) return false;
        
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        int totalCost = calculateBuyPrice(player, item, quantity);
        
        if (!rpgData.spendCoins(totalCost)) {
            return false;
        }
        
        ItemStack itemStack = item.createItemStack(quantity);
        
        // Apply enchantment if the item has one
        if (item.getEnchantment() != null) {
            itemStack = plugin.getEnchantmentManager().applyCustomEnchantment(itemStack, item.getEnchantment());
        }
        
        player.getInventory().addItem(itemStack);

        return true;
    }
    
    public boolean sellItem(Player player, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return false;
        
        ShopItem shopItem = findShopItem(itemStack.getType());
        if (shopItem == null) return false;
        
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        int sellPrice = calculateSellPrice(player, shopItem, itemStack.getAmount());
        
        // Bonus for enchanted items
        CustomEnchantment enchant = plugin.getEnchantmentManager().getCustomEnchantment(itemStack);
        if (enchant != null) {
            sellPrice = (int) (sellPrice * 2.5); // 150% bonus for enchanted items
        }
        
        rpgData.addCoins(sellPrice);
        player.getInventory().removeItem(itemStack);
        
        // Add trading XP

        return true;
    }

    private int calculateBuyPrice(Player player, ShopItem item, int quantity) {
        double price = item.getBuyPrice() * quantity;

        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        return Math.max(1, (int) price);
    }
    
    private int calculateSellPrice(Player player, ShopItem item, int quantity) {
        double price = item.getSellPrice() * quantity;
        
        PlayerRPGData rpgData = plugin.getRPGDataManager().getPlayerRPGData(player);
        return Math.max(1, (int) price);
    }
    
    private ShopItem findShopItem(Material material) {
        for (ShopItem item : shopItems.values()) {
            if (item.getMaterial() == material) {
                return item;
            }
        }
        return null;
    }
    
    public Map<String, ShopItem> getShopItems() {
        return shopItems;
    }
    
    public static class ShopItem {
        private final String id;
        private final Material material;
        private final String name;
        private final int buyPrice;
        private final int sellPrice;
        private final ItemRarity rarity;
        private final String description;
        private final CustomEnchantment enchantment;
        
        public ShopItem(String id, Material material, String name, int buyPrice, int sellPrice, ItemRarity rarity, String description, CustomEnchantment enchantment) {
            this.id = id;
            this.material = material;
            this.name = name;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.rarity = rarity;
            this.description = description;
            this.enchantment = enchantment;
        }
        
        public ItemStack createItemStack(int amount) {
            ItemStack item = new ItemStack(material, amount);
            ItemMeta meta = item.getItemMeta();
            
            meta.setDisplayName(rarity.getColor() + name);
            List<String> lore = new ArrayList<>();
            lore.add("§7" + description);
            lore.add("");
            
            if (enchantment != null) {
                lore.add("§5§lEnchanted:");
                lore.add("§8" + enchantment.getDescription());
                lore.add("");
            }
            
            lore.add("§7Rarity: " + rarity.getColoredName());
            lore.add("§7Value: §6" + sellPrice + " coins");
            meta.setLore(lore);
            
            item.setItemMeta(meta);
            return item;
        }
        
        // Getters
        public String getId() { return id; }
        public Material getMaterial() { return material; }
        public String getName() { return name; }
        public int getBuyPrice() { return buyPrice; }
        public int getSellPrice() { return sellPrice; }
        public ItemRarity getRarity() { return rarity; }
        public String getDescription() { return description; }
        public CustomEnchantment getEnchantment() { return enchantment; }
    }
}