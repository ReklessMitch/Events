package me.reklessmitch.csgo.colls;

import com.massivecraft.massivecore.store.Coll;
import me.reklessmitch.csgo.configs.ParkourArena;

public class ParkourArenaColl extends Coll<ParkourArena> {
    private static final ParkourArenaColl i = new ParkourArenaColl();
    public static ParkourArenaColl get() { return i; }
}
