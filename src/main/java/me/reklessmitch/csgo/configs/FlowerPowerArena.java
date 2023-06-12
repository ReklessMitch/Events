package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.colls.FlowerPowerArenaColl;
import me.reklessmitch.csgo.utils.SpawnLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
public class FlowerPowerArena extends Entity<FlowerPowerArena> {

    boolean active = false;
    SpawnLocation redSpawn = new SpawnLocation(new Location(Bukkit.getWorld("world"), 0, 0, 0));
    SpawnLocation blueSpawn = new SpawnLocation(new Location(Bukkit.getWorld("world"), 0, 0, 0));
    List<SpawnLocation> redSpawnPoints = List.of(new SpawnLocation(new Location(Bukkit.getWorld("world"), 0, 0, 0)));
    List<SpawnLocation> blueSpawnPoints = List.of(new SpawnLocation(new Location(Bukkit.getWorld("world"), 0, 0, 0)));

    public static FlowerPowerArena get(Object oid) {
        return FlowerPowerArenaColl.get().get(oid);
    }

    @Override
    public FlowerPowerArena load(@NotNull FlowerPowerArena that)
    {
        super.load(that);
        return this;
    }

}
