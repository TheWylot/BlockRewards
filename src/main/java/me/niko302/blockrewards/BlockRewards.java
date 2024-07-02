package me.niko302.blockrewards;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import lombok.Getter;
import me.niko302.blockrewards.commands.BlockRewardsCommand;
import me.niko302.blockrewards.config.ConfigManager;
import me.niko302.blockrewards.listeners.BlockBreakListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class BlockRewards extends JavaPlugin implements Listener {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Initialize ConfigManager
        configManager = new ConfigManager(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        // Register commands and tab completer
        BlockRewardsCommand commandExecutor = new BlockRewardsCommand(this);
        getCommand("blockrewards").setExecutor(commandExecutor);
        getCommand("blockrewards").setTabCompleter(commandExecutor);

        // Initialize metrics
        new Metrics(this, 22496);

        // Initialize update checker
        new UpdateChecker(this, UpdateCheckSource.SPIGOT, "117535")
                .setNotifyRequesters(false)
                .setNotifyOpsOnJoin(false)
                .setUserAgent(UserAgentBuilder.getDefaultUserAgent())
                .checkEveryXHours(12)
                .onSuccess((commandSenders, latestVersion) -> {
                    String messagePrefix = configManager.getPrefix();
                    String currentVersion = getDescription().getVersion();

                    if (currentVersion.equalsIgnoreCase(latestVersion)) {
                        String updateMessage = color(messagePrefix + "&aYou are using the latest version of BlockRewards!");
                        Bukkit.getConsoleSender().sendMessage(updateMessage);
                        Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(updateMessage));
                        return;
                    }

                    List<String> updateMessages = List.of(
                            color(messagePrefix + "&cYour version of BlockRewards is outdated!"),
                            color(String.format(messagePrefix + "&cYou are using %s, latest is %s!", currentVersion, latestVersion)),
                            color(messagePrefix + "&cDownload latest here:"),
                            color("&6")
                    );

                    Bukkit.getConsoleSender().sendMessage(updateMessages.toArray(new String[]{}));
                    Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(updateMessages.toArray(new String[]{})));
                })
                .onFail((commandSenders, e) -> {}).checkNow();
    }

    @Override
    public void onDisable() {
    }

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}