package me.reklessmitch.csgo.utils;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SerLocation {

    String worldName;
    double x;
    double y;
    double z;
    float yaw;
    float pitch;

    public SerLocation(Location location) {
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public BlockVector3 getVector3(){
        return BlockVector3.at(x, y, z);
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public void teleport(Player player){
        player.teleport(new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));
    }
}