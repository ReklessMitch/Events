package me.reklessmitch.csgo.games;

import lombok.Getter;
import me.reklessmitch.csgo.configs.FFAArena;
import me.reklessmitch.csgo.configs.Kit;
import org.bukkit.entity.Player;
import java.util.Map;

@Getter
public class FFAGame extends Game{

    // Spleef -> Extend
    // FFA
    // OIAC -> Extend (One in the chamber)
    Map<Player, Integer> kills;
    FFAArena arena;
    int maxPlayers = 50;
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
