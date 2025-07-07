package sonemc.soneRPG.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.gui.RaceSelectionGUI;

public class RaceCommand implements CommandExecutor {

    private final SoneRPG plugin;

    public RaceCommand(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (plugin.getRPGDataManager().getPlayerRPGData(player).hasChosenRace()) {
            player.sendMessage("§cYou have already chosen your race!");
            return true;
        }
        
        RaceSelectionGUI gui = new RaceSelectionGUI(plugin, player);
        gui.open();
        
        return true;
    }
}