package me.niko302.blockrewards.config;

import lombok.AccessLevel;
import lombok.Getter;
import me.niko302.blockrewards.BlockRewards;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ConfigManager {

    @Getter(AccessLevel.NONE)
    private final Pattern hexColorExtractor = Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");

    private final BlockRewards plugin;
    private FileConfiguration config;
    private String prefix;
    private boolean disableSilkTouch;
    private boolean suppressCommandFeedback;
    private List<String> disabledWorlds;
    private String toggleMessageOn;
    private String toggleMessageOff;

    public ConfigManager(BlockRewards plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
        loadConfig();
    }

    public void saveDefaultConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadConfig();
    }

    private void loadConfig() {
        prefix = color(config.getString("prefix", "&7[ BlockRewards] &7]"));
        disableSilkTouch = config.getBoolean("disable-silk-touch", true);
        suppressCommandFeedback = config.getBoolean("suppress-command-feedback", true);
        disabledWorlds = config.getStringList("disabled-worlds");
        toggleMessageOn = config.getString("messages.toggle-on", "&aMessages have been enabled.");
        toggleMessageOff = config.getString("messages.toggle-off", "&cMessages have been disabled.");
    }

    public String color(String message) {
        String coloredMessage = ChatColor.translateAlternateColorCodes('&', message);

        Matcher matcher = hexColorExtractor.matcher(coloredMessage);

        while (matcher.find()) {
            String hexColor = matcher.group();
            coloredMessage = coloredMessage.replace(hexColor, ChatColor.of(hexColor).toString());
        }

        return coloredMessage;
    }

    public ConfigurationSection getCustomRewardConfig(String rewardKey) {
        return config.getConfigurationSection(rewardKey);
    }
}