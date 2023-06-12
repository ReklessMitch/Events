package me.reklessmitch.csgo.games;

import com.massivecraft.massivecore.Engine;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.MiniGames;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

@Getter
public class Game extends Engine {

    private static final Game i = new Game();
    public static Game get() { return i; }

    @Setter boolean active = false;
    int gameID;
    List<Player> players = new ArrayList<>();

    public Game() {
        gameID = MiniGames.get().getNewGameID();
    }

    public void start() {
    }


    public void addPlayer(Player player){
        players.add(player);
        start();
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event){
        players.remove(event.getPlayer());
    }


    @EventHandler(ignoreCancelled = true)
    public void onLogout(PlayerQuitEvent event) {
        players.remove(event.getPlayer());
    }
}
