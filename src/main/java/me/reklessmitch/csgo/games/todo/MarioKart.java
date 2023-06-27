package me.reklessmitch.csgo.games.todo;

import com.massivecraft.massivecore.util.MUtil;
import dev.lone.itemsadder.api.CustomEntity;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.utils.SerLocation;
import org.bukkit.entity.Player;

import java.util.List;

public class MarioKart extends Game {

    Arena arena;
    List<String> carColours = List.of("green", "white");

    public MarioKart(Arena arena) {
        super();
        this.arena = arena;
        arena.setActive(true);
        setMaxPlayers(arena.getSpawnPoints().size());
    }

    @Override
    public void startGame() {
        spawnCarsAndPlayers();
    }

    public void spawnCarsAndPlayers(){
        List<SerLocation> spawnPoints = arena.getSpawnPoints();
        List<Player> players = uuidToPlayer(getPlayers()).stream().toList();
        for(int i = 0; i < spawnPoints.size(); i++){
            CustomEntity carBlock = CustomEntity.spawn("iavehicles:" + MUtil.random(carColours) + "_go_cart", spawnPoints.get(i).getLocation());
            carBlock.addPassenger(players.get(i));
        }
    }

}
