package me.reklessmitch.csgo.utils;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.UUID;

// TODO: 03/07/2023 Remove this
public class SerLocation {

    // These Cannot be final because MCORE needs to be able to serialize them

    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

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

    public void teleport(UUID playerID){
        Player player = Bukkit.getPlayer(playerID);
        if(player == null) {
            Bukkit.broadcastMessage("Player is null - ERROR");
            return;
        }
        player.teleport(new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));
    }
}
