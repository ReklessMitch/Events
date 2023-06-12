package me.reklessmitch.csgo.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpawnLocation {

    String worldName;
    double x;
    double y;
    double z;
    float yaw;
    float pitch;

    public SpawnLocation(Location location) {
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public void teleport(Player player){
        player.teleport(new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));
    }
}
