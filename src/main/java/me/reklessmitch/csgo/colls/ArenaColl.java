package me.reklessmitch.csgo.colls;

import com.massivecraft.massivecore.store.Coll;
import me.reklessmitch.csgo.configs.Arena;

public class ArenaColl extends Coll<Arena> {

    private static final ArenaColl i = new ArenaColl();
    public static ArenaColl get() { return i; }
}
