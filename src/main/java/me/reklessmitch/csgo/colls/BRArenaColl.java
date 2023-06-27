package me.reklessmitch.csgo.colls;

import com.massivecraft.massivecore.store.Coll;
import me.reklessmitch.csgo.configs.BRArena;
import me.reklessmitch.csgo.configs.FFAArena;

public class BRArenaColl extends Coll<BRArena> {

    private static final BRArenaColl i = new BRArenaColl();
    public static BRArenaColl get() { return i; }

}
