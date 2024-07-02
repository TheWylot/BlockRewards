package me.niko302.blockrewards.listeners;

import me.niko302.blockrewards.BlockRewards;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BlockBreakListener implements Listener {

    private final BlockRewards plugin;
    private final Random random;

    public BlockBreakListener(BlockRewards plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();
        Material blockType = event.getBlock().getType();

        if (plugin.getConfigManager().getDisabledWorlds().contains(worldName)) {
            return;
        }

        if (plugin.getConfigManager().isDisableSilkTouch()) {
            ItemStack tool = player.getInventory().getItemInMainHand();
            if (tool != null && tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
                return;
            }
        }

        for (String key : plugin.getConfig().getConfigurationSection("").getKeys(false)) {
            ConfigurationSection rewardConfig = plugin.getConfigManager().getCustomRewardConfig(key);
            if (rewardConfig != null) {
                Material rewardBlock = Material.valueOf(rewardConfig.getString("Material", "DIAMOND_ORE"));
                if (blockType == rewardBlock) {
                    double chance = rewardConfig.getDouble("chance", 0);
                    if (random.nextDouble() * 100 < chance) {
                        String message = rewardConfig.getString("message", "");
                        String permission = rewardConfig.getString("permission", "");

                        if (player.hasPermission(permission)) {
                            if (plugin.getConfigManager().isSuppressCommandFeedback()) {
                                // Suppress command feedback
                                boolean previousCommandFeedback = Boolean.parseBoolean(String.valueOf(player.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)));
                                player.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);

                                for (String command : rewardConfig.getStringList("commands")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getName()));
                                }

                                // Restore command feedback
                                player.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, previousCommandFeedback);
                            } else {
                                for (String command : rewardConfig.getStringList("commands")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getName()));
                                }
                            }

                            player.sendMessage(plugin.getConfigManager().color(plugin.getConfigManager().getPrefix() + message));
                        }
                    }
                }
            }
        }
    }
}