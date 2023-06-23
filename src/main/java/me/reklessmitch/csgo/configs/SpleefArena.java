package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.colls.SpleefArenaColl;
import me.reklessmitch.csgo.utils.SerLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SpleefArena extends Entity<SpleefArena> {

    String name = "";
    SerLocation spawnLocation = new SerLocation(new Location(Bukkit.getWorld(MConf.get().eventWorld), 0, 0, 0));
    boolean active = false;
    // List of floors
    int floorRadius = 25;
    List<SerLocation> removeFloor = new ArrayList<>();
    List<SerLocation> floors = new ArrayList<>();


    // -------------------------------------------- //
    public static SpleefArena get(Object oid) {
        return SpleefArenaColl.get().get(oid);
    }

    @Override
    public SpleefArena load(@NotNull SpleefArena that){
        super.load(that);
        return this;
    }

    public void addFloor(Location location) {
        floors.add(new SerLocation(location));
    }

    public void addRemoveFloor(Location location) {
        removeFloor.add(new SerLocation(location));
    }
}
