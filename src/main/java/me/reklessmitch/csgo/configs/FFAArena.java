package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.colls.FFAArenaColl;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
@Setter
public class FFAArena extends Entity<FFAArena> {

    boolean active = false;
    Set<String> allowedKits = new HashSet<>();

    public static FFAArena get(Object oid) {
        return FFAArenaColl.get().get(oid);
    }

    @Override
    public FFAArena load(@NotNull FFAArena that)
    {
        super.load(that);
        return this;
    }

}
