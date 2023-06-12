package me.reklessmitch.csgo.colls;

import com.massivecraft.massivecore.store.Coll;
import me.reklessmitch.csgo.configs.Kit;

public class KitColl extends Coll<Kit> {

    private static final KitColl i = new KitColl();
    public static KitColl get() { return i; }

}
