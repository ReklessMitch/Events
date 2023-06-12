package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import me.reklessmitch.csgo.colls.SpleefArenaColl;
import me.reklessmitch.csgo.colls.TeamArenaColl;
import me.reklessmitch.csgo.utils.SpawnLocation;
import org.jetbrains.annotations.NotNull;

public class SpleefArena extends Entity<SpleefArena> {

    SpawnLocation spawnLocation;
    boolean active = false;
    // List of floors

    // -------------------------------------------- //
    public static SpleefArena get(Object oid) {
        return SpleefArenaColl.get().get(oid);
    }

    @Override
    public SpleefArena load(@NotNull SpleefArena that)
    {
        super.load(that);
        return this;
    }
}
