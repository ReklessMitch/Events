package me.reklessmitch.csgo.games;

import com.massivecraft.massivecore.Engine;
import it.endlessgames.voidteleport.api.VoidTeleportEvent;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.utils.DisplayItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@Getter
@Setter
public class Game extends Engine {

    private static final Game i = new Game();
    public static Game get() { return i; }

    boolean active = false;
    boolean isStarting = false;
    int gameID;


    DisplayItem displayItem = new DisplayItem(Material.DIAMOND, "game", List.of("lore"), 0);

    Set<UUID> players = new HashSet<>();
    int maxPlayers = 20;
    int minPlayers = 2;


    public Game() {
        gameID = MiniGames.get().getNewGameID();
    }

    public ItemStack getDisplay(){
        ItemStack it = displayItem.getGuiItem();
        ItemMeta meta = it.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&a" + players.size() + "&f/&c" + maxPlayers));
        meta.setLore(lore);
        it.setItemMeta(meta);
        return it;
    }

    public void start() {
        setAllPlayersToSurvival();
        setActive(true);

    }

    public void end(){
        MiniGames.get().getPlayersInGame().removeAll(getPlayers());
        MiniGames.get().getGames().remove(this);
        getPlayers().forEach(playerID -> {
            resetPlayer(playerID);
            Bukkit.getPlayer(playerID).teleport(MiniGames.get().getSpawnWorld().getSpawnLocation());
        });
        players.clear();
        setActive(false);
    }

    public void setAllPlayersToSurvival(){
        getPlayers().forEach(player -> Bukkit.getPlayer(player).setGameMode(GameMode.SURVIVAL));
    }

    public void addPlayer(Player player, String txt){
        UUID uuid = player.getUniqueId();
        Set<UUID> playersInGame = MiniGames.get().getPlayersInGame();
        if(!playersInGame.contains(uuid)){
            players.add(uuid);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have been added to the queue for " + txt));
            playersInGame.add(uuid);
            start();
        }else{
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are already queued for a game!"));
        }
    }

    public void resetPlayer(UUID player){
        Player p = Bukkit.getPlayer(player);
        p.getInventory().clear();
        p.setHealth(20);
        p.setFoodLevel(20);
    }

    public void removePlayer(UUID player) {
        this.getPlayers().remove(player);
        MiniGames.get().getPlayersInGame().remove(player);
        if(Bukkit.getOfflinePlayer(player).isOnline()){
            resetPlayer(player);
            Bukkit.getPlayer(player).teleport(MiniGames.get().getSpawnWorld().getSpawnLocation());
        }
    }

    public void voidEvent(VoidTeleportEvent event){
        removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLogout(PlayerQuitEvent event) {
        for(Game game : MiniGames.get().getGames()) {
            if(game.getPlayers().contains(event.getPlayer().getUniqueId())){
                game.removePlayer(event.getPlayer().getUniqueId());
                return;
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onLogin(PlayerLoginEvent event){
        event.getPlayer().teleport(MiniGames.get().getSpawnWorld().getSpawnLocation());
    }

    public void setStarting(boolean b) {
        isStarting = b;
    }
}
