package dev.sepd.sepd_rekits;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RegionManager {

    private Map<String, Location> regions;  // Store region locations
    private Map<String, Map<String, ItemStack[]>> kits;  // Store kits for each region

    public RegionManager() {
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

    public String getRegionName(Location location) {
        // Implement logic to determine if the location is within a region
        // You might iterate through regions and check if the location is within the boundaries
        for (Map.Entry<String, Location> entry : regions.entrySet()) {
            String regionName = entry.getKey();
            Location regionLocation = entry.getValue();
            // Implement your logic to check if the location is within the region
            if (isLocationWithinRegion(location, regionLocation)) {
                return regionName;
            }
        }
        return null;
    }

    private boolean isLocationWithinRegion(Location location, Location regionLocation) {
        // Implement logic to check if the location is within the region
        // You might compare coordinates, check distances, etc.
        // This will depend on how you define and handle regions
        return location.distance(regionLocation) < 10;  // Placeholder distance check
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

    // Additional methods and logic related to region management can be added here

    // You might also want methods to handle selecting kits, saving kits, etc.
    // The exact implementation will depend on your plugin's requirements

    // For example, a method to select a kit for a player in a specific region
    public void selectKit(Player player, String regionName, String kitName) {
        // Implement logic to set the player's selected kit based on the region and kit name
        ItemStack[] selectedKit = getKit(regionName, kitName);
        if (selectedKit != null) {
            player.getInventory().setContents(selectedKit);
        }
    }
}

