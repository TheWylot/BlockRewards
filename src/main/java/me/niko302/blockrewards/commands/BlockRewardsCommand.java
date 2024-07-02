package me.niko302.blockrewards.commands;

import me.niko302.blockrewards.BlockRewards;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        displayHelp(sender);
        return true;
    }

    private void displayHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Available commands:");
        sender.sendMessage(ChatColor.YELLOW + "/blockrewards reload - Reload the plugin configuration.");
        // Add other commands if needed
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("blockrewards")) {
            if (args.length == 1) {
                return Arrays.asList("reload");
            }
        }

        if (args.length > 1 && args[args.length - 2].equalsIgnoreCase("permission")) {
            String lastArg = args[args.length - 1].toLowerCase();

            // Get permissions from config
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    ConfigurationSection rewardConfig = section.getConfigurationSection(key);
                    if (rewardConfig != null) {
                        String permission = rewardConfig.getString("permission", "");
                        if (permission != null && permission.toLowerCase().startsWith(lastArg)) {
                            completions.add(permission);
                        }
                    }
                }
            }
        }

        return completions;
    }
}