package me.niko302.blockrewards.commands;

import me.niko302.blockrewards.BlockRewards;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BlockRewardsCommand implements TabExecutor, TabCompleter {

    private final BlockRewards plugin;

    public BlockRewardsCommand(BlockRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            displayHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("blockrewards.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            plugin.getConfigManager().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
            return true;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
                return true;
            }

            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();
            boolean isToggled = plugin.togglePlayerMessage(playerUUID);
            String message = isToggled ? plugin.getConfigManager().getToggleMessageOn() : plugin.getConfigManager().getToggleMessageOff();
            player.sendMessage(plugin.getConfigManager().color(plugin.getConfigManager().getPrefix() + message));
            return true;
        }

        displayHelp(sender);
        return true;
    }

    private void displayHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Available commands:");
        sender.sendMessage(ChatColor.YELLOW + "/blockrewards reload - Reload the plugin configuration.");
        sender.sendMessage(ChatColor.YELLOW + "/blockrewards toggle - Toggle reward messages.");
        // Add other commands if needed
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("blockrewards")) {
            if (args.length == 1) {
                return Arrays.asList("reload", "toggle");
            }
        }

        return completions;
    }
}