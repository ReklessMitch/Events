package me.reklessmitch.csgo.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// TODO: 03/07/2023 Oriented object programming
public class TeleportUtils {

    public static void spawnPlayersInRadius(@NotNull Location center, double radius, @NotNull Set<Player> players) {
        players.forEach(player -> {
            Location randomLocation = getRandomLocationInRadius(center, radius);
            Location topBlockLocation = getTopBlockLocation(randomLocation);
            player.teleport(topBlockLocation);
        });
    }

    public static @NotNull Location getRandomLocationInRadius(@NotNull Location center, double radius) {
        double angle = Math.random() * Math.PI * 2; // Random angle in radians
        double x = center.getX() + radius * Math.cos(angle); // X coordinate of the random location
        double z = center.getZ() + radius * Math.sin(angle); // Z coordinate of the random location
        double y = center.getWorld().getHighestBlockYAt((int) x, (int) z); // Y coordinate of the top block

        return new Location(center.getWorld(), x, y + 1, z);
    }

    public static @NotNull Location getTopBlockLocation(@NotNull Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        int y = world.getHighestBlockYAt(x, z);

        return new Location(world, x, y, z);
    }
}
