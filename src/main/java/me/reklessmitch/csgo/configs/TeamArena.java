package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.colls.TeamArenaColl;
import me.reklessmitch.csgo.utils.SerLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TeamArena extends Entity<TeamArena> {

    String name = "";
    List<SerLocation> team1Spawns = new ArrayList<>();
    List<SerLocation> team2Spawns = new ArrayList<>();
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

    public void addSpawnLocation(int team, Location location) {
        if(team == 1){
            team1Spawns.add(new SerLocation(location));
        }else if(team == 2){
            team2Spawns.add(new SerLocation(location));
        }
        this.changed();
    }
}
