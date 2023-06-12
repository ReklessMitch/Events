package me.reklessmitch.csgo.colls;

import com.massivecraft.massivecore.store.Coll;
import me.reklessmitch.csgo.configs.TeamArena;

public class TeamArenaColl extends Coll<TeamArena> {

    private static final TeamArenaColl i = new TeamArenaColl();
    public static TeamArenaColl get() { return i; }

}
