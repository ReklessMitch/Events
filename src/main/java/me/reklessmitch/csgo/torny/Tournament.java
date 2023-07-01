package me.reklessmitch.csgo.torny;


import com.massivecraft.massivecore.mixin.MixinTitle;
import lombok.Getter;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.configs.Kit;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.games.duel.Duel;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.DisplayItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvert;

@Getter
public class Tournament implements Listener {

    private final DisplayItem displayItem =
            new DisplayItem(Material.DIAMOND_SWORD,
                    "&cDuel Tournament",
                    List.of("&71v1 duel tournament"),
                    0);
    private final int teamsAmount;
    private static final int TEAM_SIZE = 1;
    private final List<UUID> players = new ArrayList<>();
    private boolean started = false;
    private final List<Game> activeGames = new ArrayList<>();
    private final List<Arena> arenas;
    private final Kit kit;

    public Tournament(List<Arena> arenas, Kit kit, int teamsAmount) {
        this.teamsAmount = teamsAmount;
        this.arenas = arenas;
        this.kit = kit;
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());

    }

    public void addPlayer(UUID player){
        if(started || players.contains(player) || MiniGames.get().getPlayersInGame().contains(player)) return;
        players.add(player);
        MiniGames.get().getPlayersInGame().add(player);
        if(players.size() == teamsAmount * TEAM_SIZE){
            startCoolDown();
        }
    }

    public void removePlayer(UUID player){
        players.remove(player);
        MiniGames.get().getPlayersInGame().remove(player);
    }

    private void shuffleAndAddToGames(){
        Collections.shuffle(players);
        int gamesCreated = 0;
        for (int i = 0; i < players.size(); i += 2) {
            Game game = new Duel(arenas.get(gamesCreated), kit); // @TODO Need to get Arenas
            gamesCreated++;
            game.addPlayer(idConvert(players.get(i)), displayItem.itemName());
            game.addPlayer(idConvert(players.get(i + 1)), displayItem.itemName());
            Bukkit.broadcastMessage(idConvert(players.get(i)).getName() + " vs " + idConvert(players.get(i + 1)).getName());
            activeGames.add(game);
        }
    }
    public void start(){
        started = true;
        shuffleAndAddToGames();
    }

    public void startCoolDown(){
        new Countdown(10).onTick(tick -> {
            if(tick % 5 == 0 || tick <= 5){
                players.forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 20, 0,
                        "&7Tournament starting in...", "&c&l" + tick));
            }
        }).onComplete(this::start).start(MiniGames.get());
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e){
        removePlayer(e.getWinner());
        activeGames.remove(e.getGame());
        Bukkit.broadcastMessage("Games Left to finish" + activeGames.size());
        if(activeGames.isEmpty()){
            if(players.size() == 1){
                // @TODO Winner
                Bukkit.broadcastMessage("Winner: " + players.get(0).toString());
            }else {
                shuffleAndAddToGames();
            }
        }
    }

}
