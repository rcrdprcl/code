package sonemc.soneRPG.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sonemc.soneRPG.SoneRPG;

public class SoneRPGCommand implements CommandExecutor {
    
    private final SoneRPG plugin;
    
    public SoneRPGCommand(SoneRPG plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7[§bSoneRPG§7] §fPlugin by §aSoneMC");
            sender.sendMessage("§7Version: §f" + plugin.getDescription().getVersion());
            sender.sendMessage("§7Commands: §f/sonerpg level [player] [level]");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("level")) {
            if (!sender.hasPermission("sonerpg.admin")) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }
            
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    int level = plugin.getRPGLevelManager().getPlayerLevel(player);
                    sender.sendMessage("§7Your RPG level: §a" + level);
                } else {
                    sender.sendMessage("§cPlease specify a player.");
                }
                return true;
            }
            
            if (args.length == 2) {
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }
                
                int level = plugin.getRPGLevelManager().getPlayerLevel(target);
                sender.sendMessage("§7" + target.getName() + "'s RPG level: §a" + level);
                return true;
            }
            
            if (args.length == 3) {
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }
                
                try {
                    int level = Integer.parseInt(args[2]);
                    plugin.getRPGLevelManager().setPlayerLevel(target, level);
                    sender.sendMessage("§7Set " + target.getName() + "'s RPG level to §a" + level);
                    target.sendMessage("§7Your RPG level has been set to §a" + level);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid level number.");
                }
                return true;
            }
        }
        
        return false;
    }
}