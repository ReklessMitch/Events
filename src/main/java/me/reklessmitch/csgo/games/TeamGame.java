package me.reklessmitch.csgo.games;

import lombok.Getter;
import me.reklessmitch.csgo.configs.TeamArena;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

@Getter
public class TeamGame extends Game{
    TeamArena arena;
    Map<Integer, Set<Player>> teams;

    public TeamGame(TeamArena arena) {
        super();
        this.arena = arena;
    }

}
