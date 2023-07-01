package me.reklessmitch.csgo.games.todo;

import dev.lone.itemsadder.api.*;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.utils.DisplayItem;
import me.reklessmitch.csgo.utils.SerLocation;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import java.util.List;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvertList;

public class MarioKart extends Game {

    Arena arena;
    List<String> carColours = List.of("green", "white");

    public MarioKart(Arena arena) {
        super();
        this.arena = arena;
        arena.setActive(true);
        setMaxPlayers(arena.getSpawnPoints().size());
        setDisplayItem(new DisplayItem(
                Material.PAPER,
                "&c&lMARIO KART - " + arena.getName(),
                List.of("&7Race to the finish!"),
                10045
        ));
    }

    @Override
    public void startGame() {
        spawnCarsAndPlayers();
    }

    public void spawnCarsAndPlayers() {
        List<SerLocation> spawnPoints = arena.getSpawnPoints();
        List<Player> players = idConvertList(getPlayers()).stream().toList();
        for (int i = 0; i < spawnPoints.size(); i++) {
            CustomStack s = CustomStack.getInstance("iavehicles:black_go_cart");
            System.out.println(s.getNamespacedID());

        }
    }

}
