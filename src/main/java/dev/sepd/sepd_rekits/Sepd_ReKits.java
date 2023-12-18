package dev.sepd.sepd_rekits;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Sepd_ReKits extends JavaPlugin implements Listener {

    private RegionManager regionManager;
    private Map<Player, Location> wandSelections;
    private Map<String, String> messages;  // Map to store messages

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        // Initialize the RegionManager
        this.regionManager = new RegionManager();
        this.wandSelections = new HashMap<>();
        this.messages = new HashMap<>();

        // Load messages from config
        loadMessages();

        // Register the commands
        getCommand("createkit").setExecutor(this);
        getCommand("deletekit").setExecutor(this);
        getCommand("deleteregion").setExecutor(this);
        getCommand("rekit").setExecutor(this);
    }

    private void loadMessages() {
        // Load messages from config.yml
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        for (String key : getConfig().getConfigurationSection("messages").getKeys(false)) {
            messages.put(key, getConfig().getString("messages." + key));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null && isInSpecificRegion(killer)) {
            // Clear killer's inventory first
            clearInventory(killer);

            // Apply kit after a delay (1 tick)
            Bukkit.getScheduler().runTaskLater(this, () -> applyKit(killer), 1L);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.BLAZE_ROD) {
            // Handle wand selection logic
            handleWandSelection(player, event.getClickedBlock().getLocation());
        }
    }

    private void handleWandSelection(Player player, Location location) {
        if (!wandSelections.containsKey(player)) {
            player.sendMessage(colorize(messages.get("wand-selection-first")).replace("%location%", location.toString()));
            wandSelections.put(player, location);
        } else {
            Location firstPoint = wandSelections.get(player);
            player.sendMessage(colorize(messages.get("wand-selection-second")).replace("%location%", location.toString()));

            // Assuming RegionManager has a method to create a region based on two points
            regionManager.createRegion(player.getName() + "_region", firstPoint, location);

            // Clear the selection for this player
            wandSelections.remove(player);
        }
    }

    private boolean isInSpecificRegion(Player player) {
        Location location = player.getLocation();

        // Check if the player is in a specific region using RegionManager
        String regionName = regionManager.getRegionName(location);
        return regionName != null;
    }

    private void applyKit(Player player) {
        // Get the region the player is in
        Location location = player.getLocation();
        String regionName = regionManager.getRegionName(location);

        if (regionName != null) {
            // Apply the kit to the player based on the region
            String kitName = "yourKitName"; // Replace with the actual kit name you want to retrieve
            ItemStack[] kit = regionManager.getKit(regionName, kitName);
            if (kit != null) {
                player.getInventory().setContents(kit);
                player.sendMessage(colorize(messages.get("kit-received")).replace("%region%", regionName));
            } else {
                player.sendMessage(colorize(messages.get("no-kit-defined")).replace("%region%", regionName));
            }
        }
    }

    private void clearInventory(Player player) {
        player.getInventory().clear();
        player.sendMessage(colorize(messages.get("inventory-cleared")));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize("&cOnly players can use this command."));
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("createkit")) {
            if (args.length >= 2) {
                String regionName = args[0];
                String kitName = args[1];
                ItemStack[] kit = player.getInventory().getContents();

                regionManager.setKit(regionName, kitName, kit);
                player.sendMessage(colorize(messages.get("kit-saved")).replace("%kit%", kitName).replace("%region%", regionName));
                return true;
            } else {
                player.sendMessage(colorize("&cUsage: /createkit <regionName> <kitName>"));
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("deletekit")) {
            if (args.length >= 2) {
                String regionName = args[0];
                String kitName = args[1];

                regionManager.deleteKit(regionName, kitName);
                player.sendMessage(colorize(messages.get("kit-deleted")).replace("%kit%", kitName).replace("%region%", regionName));
                return true;
            } else {
                player.sendMessage(colorize("&cUsage: /deletekit <regionName> <kitName>"));
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("deleteregion")) {
            if (args.length >= 1) {
                String regionName = args[0];

                regionManager.deleteRegion(regionName);
                player.sendMessage(colorize(messages.get("region-deleted")).replace("%region%", regionName));
                return true;
            } else {
                player.sendMessage(colorize("&cUsage: /deleteregion <regionName>"));
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("rekit")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("getwand")) {
                    // Give the player a Blaze Rod as a wand
                    player.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
                    player.sendMessage(colorize(messages.get("wand-received")));
                    return true;
                } else if (args[0].equalsIgnoreCase("createregion")) {
                    if (args.length >= 2) {
                        String regionName = args[1];

                        // Assuming RegionManager has a method to create a region based on player selection
                        regionManager.createRegion(regionName, player.getLocation());
                        player.sendMessage(colorize(messages.get("region-created")).replace("%region%", regionName));
                        return true;
                    } else {
                        player.sendMessage(colorize("&cUsage: /rekit createregion <regionName>"));
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("getwand")) {
                    // Give the player a Blaze Rod as a wand
                    player.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
                    player.sendMessage(colorize(messages.get("wand-received")));
                    return true;
                } else if (args[0].equalsIgnoreCase("selectkit")) {
                    if (args.length >= 3) {
                        String regionName = args[1];
                        String kitName = args[2];

                        // Assuming RegionManager has a method to select a kit for a region
                        regionManager.selectKit(player, regionName, kitName);
                        player.sendMessage(colorize("&aSelected kit '%kit%' for region: %region%")
                                .replace("%kit%", kitName).replace("%region%", regionName));
                        return true;
                    } else {
                        player.sendMessage(colorize("&cUsage: /rekit selectkit <regionName> <kitName>"));
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private String colorize(String message) {
        return message != null ? message.replace("&", "§") : null;
    }
}
