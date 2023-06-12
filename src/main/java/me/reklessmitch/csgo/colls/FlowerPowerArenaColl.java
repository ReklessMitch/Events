package me.reklessmitch.csgo.colls;

import com.massivecraft.massivecore.store.Coll;
import me.reklessmitch.csgo.configs.FlowerPowerArena;

public class FlowerPowerArenaColl extends Coll<FlowerPowerArena> {

    private static final FlowerPowerArenaColl i = new FlowerPowerArenaColl();
    public static FlowerPowerArenaColl get() { return i; }


}
