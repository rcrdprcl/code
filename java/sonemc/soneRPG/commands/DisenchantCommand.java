package sonemc.soneRPG.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sonemc.soneRPG.SoneRPG;
import sonemc.soneRPG.enums.CustomEnchantment;

public class DisenchantCommand implements CommandExecutor {

    private final SoneRPG plugin;

    public DisenchantCommand(SoneRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (heldItem == null || heldItem.getType() == Material.AIR) {
            player.sendMessage("§cYou must be holding an item to disenchant.");
            return true;
        }
        
        CustomEnchantment enchantment = plugin.getEnchantmentManager().getCustomEnchantment(heldItem);
        if (enchantment == null) {
            player.sendMessage("§cThis item doesn't have any custom enchantments.");
            return true;
        }
        
        // Remove the enchantment from the item
        ItemStack disenchantedItem = plugin.getEnchantmentManager().removeCustomEnchantment(heldItem);
        player.getInventory().setItemInMainHand(disenchantedItem);
        
        // Add the enchantment back to player's inventory
        plugin.getEnchantmentManager().addEnchantment(player, enchantment);
        
        player.sendMessage("§a§l✦ Enchantment Recovered! §7You can now use §5" + enchantment.getDisplayName() + " §7again!");
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.2f);
        
        return true;
    }
}