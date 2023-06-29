package me.reklessmitch.csgo.games.todo;

import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.games.ffa.FFA;
import me.reklessmitch.csgo.utils.ChestUtil;
import org.bukkit.Location;

import static me.reklessmitch.csgo.utils.ChestUtil.addItemsToChests;
import static me.reklessmitch.csgo.utils.ChestUtil.getChestsInRadius;

public class HungerGames extends Game {

    Arena arena;

    public HungerGames(Arena arena) {
        this.arena = arena;
        arena.setActive(true);
        arena.changed();
    }

    @Override
    public void startGame(){
        addItemsToChests(getChestsInRadius(arena.getSpawnPoint().getLocation(), 100));
    }


}
