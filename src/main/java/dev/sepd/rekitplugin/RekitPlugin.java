package dev.sepd.rekitplugin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class RekitPlugin extends JavaPlugin implements Listener {

    private MessageManager messageManager;
    private RegionManager regionManager;
    private KitManager kitManager;

    @Override
    public void onEnable() {
        // Initialize managers
        this.messageManager = new MessageManager(this);
        this.regionManager = new RegionManager(this, messageManager);
        this.kitManager = new KitManager(this, messageManager);

        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        // Load configurations
        saveDefaultConfig();
        loadConfigurations();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("createregion")) {
                if (args.length == 2) {
                    regionManager.handleCreateRegion(player, args[0], args[1]);
                    return true;
                } else {
                    player.sendMessage("Usage: /createregion <name> <kit>");
                    return true;
                }
            } else if (command.getName().equalsIgnoreCase("rekit")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("wand")) {
                    kitManager.giveSelectionWand(player);
                    return true;
                } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    reloadConfigurations(player);
                    return true;
                }
            } else if (command.getName().equalsIgnoreCase("kitcreate")) {
                if (args.length == 1) {
                    kitManager.handleKitCreate(player, args[0]);
                    return true;
                } else {
                    player.sendMessage("Usage: /kitcreate <kitname>");
                    return true;
                }
            } else if (command.getName().equalsIgnoreCase("kitselect")) {
                if (args.length == 2) {
                    kitManager.handleKitSelect(player, args[0], args[1]);
                    return true;
                } else {
                    player.sendMessage("Usage: /kitselect <regionname> <kitname>");
                    return true;
                }
            }
        }

        return false;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            // Check if the kill happened in a defined region
            String region = regionManager.getRegionName(killer.getLocation());
            if (region != null) {
                // Get the kit associated with the region
                String kit = regionManager.getRegionKit(region);

                if (kit != null) {
                    // Give the killer the specified kit
                    kitManager.giveKit(killer, kit);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.STICK && event.getAction() == Action.RIGHT_CLICK_AIR) {
            // Handle region selection with the stick
            regionManager.handleCreateRegion(player, "defaultRegion", "defaultKit");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        // Implementation for handling player command event
    }

    private void reloadConfigurations(Player player) {
        regionManager.reloadConfigurations();
        kitManager.reloadConfigurations();
        player.sendMessage(messageManager.getFormattedMessage("reloadedConfigurations"));
    }

    private void loadConfigurations() {
        regionManager.reloadConfigurations();
        kitManager.reloadConfigurations();
        // Load other general configurations from config.yml if needed
    }
}
