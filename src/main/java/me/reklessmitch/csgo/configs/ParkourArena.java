package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.colls.ParkourArenaColl;
import me.reklessmitch.csgo.utils.SerLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParkourArena extends Entity<ParkourArena> {
    String name = "";
    SerLocation startLocation = null;
    List<SerLocation> checkPoints = new ArrayList<>();
    boolean active = false;

    public static ParkourArena get(Object oid) {
        return ParkourArenaColl.get().get(oid);
    }

    @Override
    public ParkourArena load(@NotNull ParkourArena that){
        super.load(that);
        return this;
    }


}
