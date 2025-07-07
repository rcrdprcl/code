package sonemc.soneRPG.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.gui.ShopGUI;

public class RPGShopCommand implements CommandExecutor {

    private final SoneRPG plugin;

    public RPGShopCommand(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        String category = "weapons";
        if (args.length > 0) {
            category = args[0].toLowerCase();
            if (!category.equals("weapons") && !category.equals("armor") && 
                !category.equals("consumables") && !category.equals("materials")) {
                category = "weapons";
            }
        }
        
        ShopGUI gui = new ShopGUI(plugin, player, category);
        gui.open();
        
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_CHEST_OPEN, 1.0f, 1.2f);
        
        return true;
    }
}