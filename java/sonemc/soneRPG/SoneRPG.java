package sonemc.soneRPG;

import org.bukkit.plugin.java.JavaPlugin;
import sonemc.soneRPG.listeners.*;
import sonemc.soneRPG.managers.*;
import sonemc.soneRPG.commands.*;

public class SoneRPG extends JavaPlugin {

    private RPGLevelManager rpgLevelManager;
    private HologramManager hologramManager;
    private SkillManager skillManager;
    private ConfigManager configManager;
    private EnchantmentManager enchantmentManager;
    private StatisticsManager statisticsManager;
    private RPGDataManager rpgDataManager;
    private QuestManager questManager;
    private ShopManager shopManager;
    private CraftingManager craftingManager;
    private AlchemyManager alchemyManager;
    private PoisonManager poisonManager;
    private StaminaManager staminaManager;
    private ActionBarUpdateListener actionBarListener;
    private ArmorEnchantmentListener armorEnchantmentListener;
    private AdvancedParticleListener particleListener;

    @Override
    public void onEnable() {
        printStartingMessage();

        try {
            this.configManager = new ConfigManager(this);
            this.rpgLevelManager = new RPGLevelManager(this);
            this.hologramManager = new HologramManager(this);
            this.skillManager = new SkillManager(this);
            this.enchantmentManager = new EnchantmentManager(this);
            this.statisticsManager = new StatisticsManager(this);
            this.rpgDataManager = new RPGDataManager(this);
            this.questManager = new QuestManager(this);
            this.shopManager = new ShopManager(this);
            this.craftingManager = new CraftingManager(this);
            this.alchemyManager = new AlchemyManager(this);
            this.poisonManager = new PoisonManager(this);
            this.staminaManager = new StaminaManager(this);

            this.actionBarListener = new ActionBarUpdateListener(this);
            this.armorEnchantmentListener = new ArmorEnchantmentListener(this);
            this.particleListener = new AdvancedParticleListener(this);

            getServer().getPluginManager().registerEvents(new ArmorSpeedListener(), this);
            getServer().getPluginManager().registerEvents(new MobDifficultyListener(this), this);
            getServer().getPluginManager().registerEvents(particleListener, this);
            getServer().getPluginManager().registerEvents(new HolographicHealthListener(this), this);
            getServer().getPluginManager().registerEvents(new SkillXPListener(this), this);
            getServer().getPluginManager().registerEvents(new EnhancedEnchantmentListener(this), this);
            getServer().getPluginManager().registerEvents(new ClassBonusListener(this), this);
            getServer().getPluginManager().registerEvents(new QuestProgressListener(this), this);
            getServer().getPluginManager().registerEvents(new RacialBonusListener(this), this);
            getServer().getPluginManager().registerEvents(actionBarListener, this);
            getServer().getPluginManager().registerEvents(armorEnchantmentListener, this);
            getServer().getPluginManager().registerEvents(new PoisonListener(this), this);
            getServer().getPluginManager().registerEvents(new PotionListener(this), this);
            getServer().getPluginManager().registerEvents(new MobAbilitiesListener(this), this);
            getServer().getPluginManager().registerEvents(new StaminaListener(this), this);

            if (getCommand("sonerpg") != null) {
                getCommand("sonerpg").setExecutor(new SoneRPGCommand(this));
            }
            if (getCommand("skills") != null) {
                getCommand("skills").setExecutor(new SkillsCommand(this));
            }
            if (getCommand("rpgui") != null) {
                getCommand("rpgui").setExecutor(new RPGUICommand(this));
            }
            if (getCommand("enchantforge") != null) {
                getCommand("enchantforge").setExecutor(new EnchantForgeCommand(this));
            }
            if (getCommand("disenchant") != null) {
                getCommand("disenchant").setExecutor(new DisenchantCommand(this));
            }
            if (getCommand("class") != null) {
                getCommand("class").setExecutor(new ClassCommand(this));
            }
            if (getCommand("quests") != null) {
                getCommand("quests").setExecutor(new QuestCommand(this));
            }
            if (getCommand("rpgshop") != null) {
                getCommand("rpgshop").setExecutor(new RPGShopCommand(this));
            }
            if (getCommand("race") != null) {
                getCommand("race").setExecutor(new RaceCommand(this));
            }
            if (getCommand("craft") != null) {
                getCommand("craft").setExecutor(new CraftCommand(this));
            }
            if (getCommand("alchemy") != null) {
                getCommand("alchemy").setExecutor(new AlchemyCommand(this));
            }

            saveDefaultConfig();
            configManager.createMobsConfig();
            enchantmentManager.createEnchantmentsConfig();
            statisticsManager.createStatisticsConfig();
            rpgDataManager.createRPGDataConfig();

            printStartedMessage("§a§lFULLY OPERATIONAL");

        } catch (Exception e) {
            printStartedMessage("§c§lERRORS DETECTED");
            getLogger().severe("Error during plugin initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        printStoppingMessage();

        try {
            if (actionBarListener != null) {
                actionBarListener.cleanup();
            }

            if (armorEnchantmentListener != null) {
                armorEnchantmentListener.cleanup();
            }

            if (particleListener != null) {
                particleListener.cleanup();
            }

            if (staminaManager != null) {
                staminaManager.cleanup();
            }

            if (hologramManager != null) {
                hologramManager.cleanup();
            }

            if (skillManager != null) {
                skillManager.saveAllPlayerData();
            }

            if (enchantmentManager != null) {
                enchantmentManager.saveAllPlayerData();
            }

            if (statisticsManager != null) {
                statisticsManager.saveAllPlayerData();
            }

            if (rpgDataManager != null) {
                rpgDataManager.saveAllPlayerData();
            }

            printDisabledMessage();

        } catch (Exception e) {
            getLogger().severe("Error during plugin shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printStartingMessage() {
        getLogger().info("§6═══════════════════════════════════");
        getLogger().info("§6§l          SoneRPG");
        getLogger().info("§e         Early Edition");
        getLogger().info("§6═══════════════════════════════════");
        getLogger().info("§b⚔ Initializing epic adventure...");
    }

    private void printStartedMessage(String status) {
        getLogger().info("§6═══════════════════════════════════");
        getLogger().info("§6§l          SoneRPG");
        getLogger().info("§e           " + getDescription().getVersion());
        getLogger().info("§7Status: " + status);
        getLogger().info("§6═══════════════════════════════════");
    }

    private void printStoppingMessage() {
        getLogger().info("§6═══════════════════════════════════");
        getLogger().info("§c⚔ Shutting down SoneRPG...");
        getLogger().info("§7Saving all player data...");
        getLogger().info("§6═══════════════════════════════════");
    }

    private void printDisabledMessage() {
        getLogger().info("§6═══════════════════════════════════");
        getLogger().info("§6§l      SoneRPG DISABLED");
        getLogger().info("§7      Until next adventure...");
        getLogger().info("§6═══════════════════════════════════");
    }

    public RPGLevelManager getRPGLevelManager() { return rpgLevelManager; }
    public HologramManager getHologramManager() { return hologramManager; }
    public SkillManager getSkillManager() { return skillManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public EnchantmentManager getEnchantmentManager() { return enchantmentManager; }
    public StatisticsManager getStatisticsManager() { return statisticsManager; }
    public RPGDataManager getRPGDataManager() { return rpgDataManager; }
    public QuestManager getQuestManager() { return questManager; }
    public ShopManager getShopManager() { return shopManager; }
    public CraftingManager getCraftingManager() { return craftingManager; }
    public AlchemyManager getAlchemyManager() { return alchemyManager; }
    public PoisonManager getPoisonManager() { return poisonManager; }
    public StaminaManager getStaminaManager() { return staminaManager; }
}