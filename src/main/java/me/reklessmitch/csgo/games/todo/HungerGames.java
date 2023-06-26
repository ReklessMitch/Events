package me.reklessmitch.csgo.games.todo;

import me.reklessmitch.csgo.configs.FFAArena;
import me.reklessmitch.csgo.games.ffa.FFAGame;
import org.bukkit.Location;

public class HungerGames extends FFAGame {

    public HungerGames(FFAArena arena) {
        super(arena);
    }

    private void getChestsInRadius(){
        Location location = getArena().getSpawnLocations().get(0).getLocation();
        // Get all chests in a radius of 100 blocks

    }


}
