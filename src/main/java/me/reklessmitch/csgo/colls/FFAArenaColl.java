package me.reklessmitch.csgo.colls;

import com.massivecraft.massivecore.store.Coll;
import me.reklessmitch.csgo.configs.FFAArena;

public class FFAArenaColl extends Coll<FFAArena> {

    private static final FFAArenaColl i = new FFAArenaColl();
    public static FFAArenaColl get() { return i; }

}
