package me.reklessmitch.csgo.torny;


import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinTitle;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.games.duel.Duel;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.DisplayItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import java.util.*;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvert;


public class Tournament extends Engine {

    private static final Tournament i = new Tournament();
    public static Tournament get() { return i; }

    DisplayItem displayItem = new DisplayItem(Material.DIAMOND, "Tournament", List.of("lore"), 0);
    int teamsAmount = 4;
    int teamSize = 1;
    List<UUID> players = new ArrayList<>();
    boolean started = false;
    List<Game> activeGames = new ArrayList<>();

    public Tournament() {
    }

    public void addPlayer(UUID player){
        if(started) return;
        players.add(player);
        if(players.size() == teamsAmount * teamSize){
            startCoolDown();
        }
    }

    public void removePlayer(UUID player){
        players.remove(player);
    }

    private void shuffleAndAddToGames(){
        Collections.shuffle(players);
        for (int i = 0; i < players.size(); i += 2) {
            Game game = new Duel(null); // @TODO Need to get Arenas
            game.addPlayer(idConvert(players.get(i)), displayItem.getItemName());
            game.addPlayer(idConvert(players.get(i + 1)), displayItem.getItemName());
            activeGames.add(game);
        }
    }
    public void start(){
        shuffleAndAddToGames();
    }

    public void startCoolDown(){
        new Countdown(30).onTick(tick -> {
            if(tick % 5 == 0 || tick <= 5){
                players.forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 20, 0, "&7Game starting in...", "&c&l" + tick));
            }
        }).onComplete(this::start).start(MiniGames.get());
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e){
        removePlayer(e.getWinner());
        activeGames.remove(e.getGame());
        if(activeGames.isEmpty()){
            if(players.size() == 1){
                // @TODO Winner
                Bukkit.broadcastMessage("Winner: " + players.get(0).toString());
                return;
            }
            shuffleAndAddToGames();
        }
    }

}
