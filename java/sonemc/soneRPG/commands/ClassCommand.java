package sonemc.soneRPG.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.gui.ClassSelectionGUI;

public class ClassCommand implements CommandExecutor {

    private final SoneRPG plugin;

    public ClassCommand(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (plugin.getRPGDataManager().getPlayerRPGData(player).hasChosenClass()) {
            player.sendMessage("§cYou have already chosen your class!");
            return true;
        }
        
        ClassSelectionGUI gui = new ClassSelectionGUI(plugin, player);
        gui.open();
        
        return true;
    }
}