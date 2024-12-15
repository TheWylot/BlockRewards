package me.niko302.blockrewards;

import lombok.Getter;
import me.niko302.blockrewards.commands.BlockRewardsCommand;
import me.niko302.blockrewards.config.ConfigManager;
import me.niko302.blockrewards.listeners.BlockBreakListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BlockRewards extends JavaPlugin implements Listener {

    private ConfigManager configManager;
    private File dataFile;
    private YamlConfiguration dataConfig;
    private Map<UUID, Boolean> playerMessages;

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

        // Load player message preferences
        loadData();
    }

    @Override
    public void onDisable() {
        // Save player message preferences
        saveData();
    }

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean isPlayerMessagesEnabled(UUID playerUUID) {
        return playerMessages.getOrDefault(playerUUID, true);
    }

    public boolean togglePlayerMessage(UUID playerUUID) {
        boolean isEnabled = !playerMessages.getOrDefault(playerUUID, true);
        playerMessages.put(playerUUID, isEnabled);
        return isEnabled;
    }

    private void loadData() {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            saveResource("data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        playerMessages = new HashMap<>();
        for (String key : dataConfig.getKeys(false)) {
            playerMessages.put(UUID.fromString(key), dataConfig.getBoolean(key));
        }
    }

    private void saveData() {
        for (Map.Entry<UUID, Boolean> entry : playerMessages.entrySet()) {
            dataConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}