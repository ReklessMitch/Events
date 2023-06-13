package me.reklessmitch.csgo.games;

import com.massivecraft.massivecore.Engine;
import it.endlessgames.voidteleport.api.VoidTeleportEvent;
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
    Set<Player> players = new HashSet<>();

    public Game() {
        gameID = MiniGames.get().getNewGameID();
    }

    public void start() {
    }

    public void end(){
    }

    public void addPlayer(Player player){
        players.add(player);
        start();
    }

    public void resetPlayer(Player player){
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if(players.size() == 1){
            end();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event){
        removePlayer(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void voidTPEvent(VoidTeleportEvent event){
        removePlayer(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLogout(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }
}
