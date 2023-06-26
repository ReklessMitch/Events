package me.reklessmitch.csgo.games.ffa;

import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.MUtil;
import lombok.Getter;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.FFAArena;
import me.reklessmitch.csgo.configs.Kit;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.guis.SelectKitGUI;
import me.reklessmitch.csgo.utils.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class FFAGame extends Game {

    Map<UUID, Integer> kills = new HashMap<>();
    int killsToWin = 3;
    FFAArena arena;
    Kit kit;

    public FFAGame(FFAArena arena) {
        super();
        this.arena = arena;
        arena.changed();
    }


    @Override
    public void startGame(){
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        getPlayers().forEach(p -> MUtil.random(arena.getSpawnLocations()).teleport(p));
        SelectKitGUI kitGUI = new SelectKitGUI(arena.getAllowedKits());
        kitGUI.open(getPlayers());
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), () -> {
            kit = kitGUI.getHighestVotedKit();
            getPlayers().forEach(p -> {
                kills.put(p, 0);
                reset(p);
            });
            doCountdown();
        }, 20L * 10);
    }

    private void doCountdown() {
        new Countdown(10).onTick(tick -> {
            if(tick % 5 == 0 || tick <= 5){
                getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(
                    player, 0, 20, 0, "&c&lGame begins in", tick + " seconds"));
            }}).onComplete(() -> {
                getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(
                        player, 0, 20, 0, "&c&lGO!", "&7May the odds, be forever, in your favour."));
                setHasStarted(true);
            }).start(MiniGames.get());
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID playerID = event.getPlayer().getUniqueId();
        if (!getPlayers().contains(playerID)) return;
        if (!isHasStarted()) {
            event.setCancelled(true);
        }
    }

    public void reset(UUID playerID) {
        Player player = Bukkit.getPlayer(playerID);
        player.getInventory().clear();
        kit.giveAllItems(player);
    }

    @Override
    public void end() {
        arena.setActive(false);
        arena.changed();
        super.end();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawnEvent(PlayerRespawnEvent event){
        if(!getPlayers().contains(event.getPlayer().getUniqueId())) return;
        event.setRespawnLocation(MUtil.random(arena.getSpawnLocations()).getLocation());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!getPlayers().contains(event.getPlayer().getUniqueId()) || event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        getKills().put(killer.getUniqueId(), getKills().get(killer.getUniqueId()) + 1);

        if(getKills().get(killer.getUniqueId()) >= killsToWin){
            getPlayers().forEach(player -> Bukkit.getPlayer(player).sendTitle("Game Over", killer.getName() + " won!"));
            event.setCancelled(true);
            end();
        }
    }

}
