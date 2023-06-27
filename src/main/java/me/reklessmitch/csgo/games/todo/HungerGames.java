package me.reklessmitch.csgo.games.todo;

import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.games.ffa.FFA;
import org.bukkit.Location;

public class HungerGames extends FFA {

    public HungerGames(Arena arena) {
        super(arena);
    }

    private void getChestsInRadius(){
        Location location = getArena().getSpawnPoint().getLocation();
        // Get all chests in a radius of 100 blocks

    }


}
