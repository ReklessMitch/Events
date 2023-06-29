package me.reklessmitch.csgo.games.duel;

import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.configs.Kit;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.guis.SelectKitGUI;
import me.reklessmitch.csgo.torny.GameEndEvent;
import me.reklessmitch.csgo.utils.DisplayItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.UUID;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvert;

public class Duel extends Game {

    Arena arena;
    Kit kit;

    public Duel(Arena arena, Kit kit) {
        super();
        this.arena = arena;
        this.kit = kit;
        arena.setActive(true);
        arena.changed();
        setDisplayItem(new DisplayItem(
                Material.IRON_SWORD,
                "&c&lDuel - " + arena.getName(),
                List.of("&7Last Man Standing wins!"),
                0
        ));
        setMaxPlayers(2);
    }

    private void teleportToSpawns(){
        List<UUID> players = getPlayers().stream().toList();
        arena.getSpawnPoints().get(0).teleport(players.get(0));
        arena.getSpawnPoints().get(1).teleport(players.get(1));
    }

    @Override
    public void startGame(){
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        teleportToSpawns();
        getPlayers().forEach(this::reset);
        doCountdown();
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
        Player player = idConvert(playerID);
        player.getInventory().clear();
        kit.giveAllItems(player);
    }

    @Override
    public void end() {
        arena.setActive(false);
        arena.changed();

        GameEndEvent e = new GameEndEvent(getPlayers().stream().toList().get(0), this);
        Bukkit.getServer().getPluginManager().callEvent(e);
        super.end();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!getPlayers().contains(event.getPlayer().getUniqueId())) return;
        getPlayers().remove(event.getPlayer().getUniqueId());
        end();
    }


}

