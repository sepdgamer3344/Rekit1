package dev.sepd.rekitplugin;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RegionManager {

    private final RekitPlugin plugin;
    private final MessageManager messageManager;
    private final Map<String, String> regionKits = new HashMap<>();
    private final Map<String, ConfigurationSection> kits = new HashMap<>();
    private final Map<Player, Location> regionSelections = new HashMap<>();

    public RegionManager(RekitPlugin plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }


    public void handleCreateRegion(Player player, String regionName, String kitName) {
        if (regionSelections.containsKey(player)) {
            Location point1 = regionSelections.remove(player);
            Location point2 = player.getLocation();

            createRegion(player, point1, point2, regionName, kitName);
        } else {
            regionSelections.put(player, player.getLocation());
            player.sendMessage(messageManager.getFormattedMessage("regionSelectionPoint1"));
        }
    }

    private void handleLeftClick(Player player, Location location) {
        // Implementation for handling left-click
        player.sendMessage(messageManager.getFormattedMessage("wandLeftClick", location.getX(), location.getY(), location.getZ()));
    }

    private void handleRightClick(Player player, Location location) {
        // Implementation for handling right-click
        player.sendMessage(messageManager.getFormattedMessage("wandRightClick", location.getX(), location.getY(), location.getZ()));
    }

    public String getRegionName(Location location) {
        // Implementation for determining region name based on location
        return "defaultRegion";
    }

    public String getRegionKit(String regionName) {
        return regionKits.get(regionName);
    }

    public void reloadConfigurations() {
        regionKits.clear();
        kits.clear();
        loadConfigurations();
    }

    private void createRegion(Player player, Location point1, Location point2, String regionName, String kitName) {
        regionKits.put(regionName, kitName);
        saveKit(player, kitName);

        saveKits();
        saveRegionKits();

        player.sendMessage(messageManager.getFormattedMessage("regionCreated"));
    }

    private void loadConfigurations() {
        loadKits();
        loadRegionKits();
        // Load other general configurations from config.yml if needed
    }

    private void loadKits() {
        // Implementation for loading kits from a configuration file
    }

    private void loadRegionKits() {
        // Implementation for loading region kits from a configuration file
    }

    private void saveKit(Player player, String kitName) {
        ConfigurationSection kitSection = plugin.getConfig().createSection("kits." + kitName);
        ItemStack[] items = player.getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                kitSection.set("items." + i, items[i]);
            }
        }
        kits.put(kitName, kitSection);
    }

    private void saveKits() {
        ConfigurationSection kitsSection = plugin.getConfig().createSection("kits");
        for (Map.Entry<String, ConfigurationSection> entry : kits.entrySet()) {
            kitsSection.set(entry.getKey(), entry.getValue());
        }
        plugin.saveConfig();
    }

    private void saveRegionKits() {
        plugin.getConfig().createSection("regions", regionKits);
        plugin.saveConfig();
    }
}
