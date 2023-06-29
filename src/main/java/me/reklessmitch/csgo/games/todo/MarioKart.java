package me.reklessmitch.csgo.games.todo;

import com.massivecraft.massivecore.util.MUtil;
import dev.lone.itemsadder.api.*;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.utils.DisplayItem;
import me.reklessmitch.csgo.utils.SerLocation;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;

import java.util.List;

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
        List<Player> players = uuidToPlayer(getPlayers()).stream().toList();
        for (int i = 0; i < spawnPoints.size(); i++) {
            CustomStack s = CustomStack.getInstance("iavehicles:black_go_cart");
            System.out.println(s.getNamespacedID());
            if(s instanceof CustomFurniture f){
                f.getEntity().spawnAt(spawnPoints.get(i).getLocation());
                f.getEntity().addPassenger(players.get(i));
            }
            //CustomFurniture test = CustomFurniture.spawnPreciseNonSolid("iavehicles:black_go_cart",
                    //spawnPoints.get(i).getLocation());

            //System.out.println(test.getDisplayName());

        }
    }

}
