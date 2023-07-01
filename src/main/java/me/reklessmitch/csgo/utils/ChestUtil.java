package me.reklessmitch.csgo.utils;

import com.massivecraft.massivecore.util.MUtil;
import me.reklessmitch.csgo.configs.MConf;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChestUtil {

    public static List<Chest> getChestsInRadius(@NotNull Location location, int radius) {
        List<Chest> chests = new ArrayList<>();
        int minX = location.getBlockX() - radius;
        int minY = location.getBlockY() - radius;
        int minZ = location.getBlockZ() - radius;
        int maxX = location.getBlockX() + radius;
        int maxY = location.getBlockY() + radius;
        int maxZ = location.getBlockZ() + radius;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location blockLocation = new Location(location.getWorld(), x, y, z);
                    Block block = blockLocation.getBlock();
                    if (block.getState() instanceof Chest chest) {
                        chests.add(chest);
                    }
                }
            }
        }
        return chests;
    }

    public static void addItemsToChests(@NotNull List<Chest> chests) {
        // @TODO: Add items to chests -> Needs ChestItem for % chance and amount
        for (Chest chest : chests) {
            Inventory inventory = chest.getBlockInventory();
            inventory.clear();
        }
    }
}
