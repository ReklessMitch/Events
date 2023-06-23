package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.colls.FlowerPowerArenaColl;
import me.reklessmitch.csgo.utils.SerLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class FlowerPowerArena extends Entity<FlowerPowerArena> {

    String name = "";
    boolean active = false;
    Set<SerLocation> spawns = new HashSet<>();

    public static FlowerPowerArena get(Object oid) {
        return FlowerPowerArenaColl.get().get(oid);
    }

    @Override
    public FlowerPowerArena load(@NotNull FlowerPowerArena that)
    {
        super.load(that);
        return this;
    }

    public void addSpawnLocation(SerLocation location) {
        spawns.add(location);
        this.changed();
    }
}
