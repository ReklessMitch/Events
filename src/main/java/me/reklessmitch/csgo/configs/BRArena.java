package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.colls.BRArenaColl;
import me.reklessmitch.csgo.colls.FFAArenaColl;
import me.reklessmitch.csgo.utils.SerLocation;

import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public class BRArena extends Entity<BRArena> {

    String arenaName = "";
    SerLocation middleLocation;
    boolean active;

    public static BRArena get(Object oid) {
        return BRArenaColl.get().get(oid);
    }

    @Override
    public BRArena load(@NotNull BRArena that)
    {
        super.load(that);
        return this;
    }

}
