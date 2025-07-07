package sonemc.soneRPG.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.gui.SkillsGUI;

public class SkillsCommand implements CommandExecutor {
    
    private final SoneRPG plugin;
    
    public SkillsCommand(SoneRPG plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        SkillsGUI gui = new SkillsGUI(plugin, player);
        gui.open();
        
        return true;
    }
}