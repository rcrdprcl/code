package sonemc.soneRPG.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.gui.EnchantForgeGUI;

public class EnchantForgeCommand implements CommandExecutor {

    private final SoneRPG plugin;

    public EnchantForgeCommand(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        EnchantForgeGUI gui = new EnchantForgeGUI(plugin, player);
        gui.open();
        
        return true;
    }
}