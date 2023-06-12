package me.reklessmitch.csgo.games;

import me.reklessmitch.csgo.configs.FFAArena;
import me.reklessmitch.csgo.configs.Kit;
import me.reklessmitch.csgo.utils.SerLocation;

import java.util.ArrayList;
import java.util.List;

public class FFAGame extends Game{

    // Spleef -> Extend
    // FFA
    // OIAC -> Extend (One in the chamber)

    List<SerLocation> spawnLocations = new ArrayList<>();
    int maxPlayers = 50;
    Kit kit;

    public FFAGame() {
        super();
    }

    @Override
    public void start() {
        // Pick a kit, start the game
    }

}
