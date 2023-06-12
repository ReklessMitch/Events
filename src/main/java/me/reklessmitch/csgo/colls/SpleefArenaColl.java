package me.reklessmitch.csgo.colls;

import com.massivecraft.massivecore.store.Coll;
import me.reklessmitch.csgo.configs.SpleefArena;

public class SpleefArenaColl extends Coll<SpleefArena> {

    private static final SpleefArenaColl i = new SpleefArenaColl();
    public static SpleefArenaColl get() { return i; }
}
