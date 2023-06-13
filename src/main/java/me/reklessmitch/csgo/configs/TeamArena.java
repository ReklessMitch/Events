package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.colls.TeamArenaColl;
import me.reklessmitch.csgo.utils.SerLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class TeamArena extends Entity<TeamArena> {

    Map<Integer, List<SerLocation>> teamSpawns;
    boolean active = false;

    public static TeamArena get(Object oid) {
        return TeamArenaColl.get().get(oid);
    }

    @Override
    public TeamArena load(@NotNull TeamArena that)
    {
        super.load(that);
        return this;
    }
}
