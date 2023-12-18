package dev.sepd.sepd_rekits;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionManager {

    private final Sepd_ReKits plugin; // Reference to the main plugin class
    private Map<String, Location> regions;
    private Map<String, Map<String, ItemStack[]>> kits;

    public RegionManager(Sepd_ReKits plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<>();
        this.kits = new HashMap<>();
    }

    public void createRegion(String regionName, Location location) {
        regions.put(regionName, location);
        kits.put(regionName, new HashMap<>());
    }

    public void createRegion(String regionName, Location firstPoint, Location secondPoint) {
        // Implement region creation based on two points
        // For example, you can store both points and use them as boundaries
        // This would depend on the specifics of your region handling
        // regions.put(regionName, createRegionWithTwoPoints(firstPoint, secondPoint));
        // kits.put(regionName, new HashMap<>());
    }

    public void deleteRegion(String regionName) {
        regions.remove(regionName);
        kits.remove(regionName);
    }

    public boolean isInSpecificRegion(Player player) {
        Location location = player.getLocation();
        String regionName = getRegionName(location);
        return regionName != null;
    }

    public String getRegionName(Location location) {
        for (Map.Entry<String, Location> entry : regions.entrySet()) {
            String regionName = entry.getKey();
            Location regionLocation = entry.getValue();
            if (isLocationWithinRegion(location, regionLocation)) {
                return regionName;
            }
        }
        return null;
    }

    private boolean isLocationWithinRegion(Location location, Location regionLocation) {
        return location.distance(regionLocation) < 10; // Placeholder distance check
    }

    public void setKit(String regionName, String kitName, ItemStack[] kit) {
        if (kits.containsKey(regionName)) {
            kits.get(regionName).put(kitName, kit);
        }
    }

    public void deleteKit(String regionName, String kitName) {
        if (kits.containsKey(regionName)) {
            kits.get(regionName).remove(kitName);
        }
    }

    public ItemStack[] getKit(String regionName, String kitName) {
        return kits.getOrDefault(regionName, new HashMap<>()).get(kitName);
    }

    public Map<String, ItemStack[]> getKits(String regionName) {
        return kits.get(regionName);
    }

    public void loadKits() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection kitsSection = config.getConfigurationSection("kits");

        if (kitsSection != null) {
            for (String regionName : kitsSection.getKeys(false)) {
                ConfigurationSection regionKitsSection = kitsSection.getConfigurationSection(regionName);

                if (regionKitsSection != null) {
                    for (String kitName : regionKitsSection.getKeys(false)) {
                        ItemStack[] kit = ((List<?>) regionKitsSection.getList(kitName)).toArray(new ItemStack[0]);
                        setKit(regionName, kitName, kit);
                    }
                }
            }
        }
    }

    public void saveKits() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection kitsSection = config.createSection("kits");

        for (Map.Entry<String, Map<String, ItemStack[]>> regionEntry : kits.entrySet()) {
            String regionName = regionEntry.getKey();
            Map<String, ItemStack[]> regionKits = regionEntry.getValue();

            ConfigurationSection regionKitsSection = kitsSection.createSection(regionName);

            for (Map.Entry<String, ItemStack[]> kitEntry : regionKits.entrySet()) {
                String kitName = kitEntry.getKey();
                ItemStack[] kit = kitEntry.getValue();

                // Save the kit items to the config
                regionKitsSection.set(kitName, Arrays.asList(kit));
            }
        }

        // Save the config to file
        plugin.saveConfig();
    }

    // Additional methods and logic related to region management can be added here

    public void selectKit(Player player, String regionName, String kitName) {
        ItemStack[] selectedKit = getKit(regionName, kitName);
        if (selectedKit != null) {
            player.getInventory().setContents(selectedKit);
        }
    }
}
