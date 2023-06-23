package me.reklessmitch.csgo.games;

import lombok.Getter;
import me.reklessmitch.csgo.configs.FFAArena;
import me.reklessmitch.csgo.configs.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class FFAGame extends Game{

    Map<UUID, Integer> kills = new HashMap<>();
    FFAArena arena;
    Kit kit;

    public FFAGame(FFAArena arena) {
        super();
        this.arena = arena;
    }

    @Override
    public void start() {
        // Pick a kit, start the game
    }

}
