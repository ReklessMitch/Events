package me.reklessmitch.csgo.games;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinTitle;
import it.endlessgames.voidteleport.api.VoidTeleportEvent;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.DisplayItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvert;

@Getter
@Setter
public class Game extends Engine {

    private static final Game i = new Game();
    public static Game get() { return i; }

    private boolean active = false;
    private boolean isStarting = false;
    private boolean hasStarted = false;
    private int gameID;

    private DisplayItem displayItem = new DisplayItem(Material.DIAMOND, "game", List.of("lore"), 0);

    private Set<UUID> players = new HashSet<>();
    private int maxPlayers = 20;
    private int minPlayers = 2;


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

    public void startGame(){}

    public void start() {
        if (getPlayers().size() >= getMinPlayers() && !isStarting()){
            setStarting(true);
            new Countdown(10).onTick(tick -> {
                if(tick % 5 == 0 || tick <= 5){
                    getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 20, 0, "&7Game starting in...", "&c&l" + tick));
                }
            }).onComplete(() -> {
                if(getPlayers().size() >= getMinPlayers()) {
                    setAllPlayersToSurvival();
                    setActive(true);
                    startGame();
                }
                else {
                    setStarting(false);
                    getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 20, 0, "&c&lNot enough players!", "&7Game cancelled!"));
                }
            }).start(MiniGames.get());
        }

    }

    public void doCountdown() {
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

    public void end(){
        MiniGames.get().getPlayersInGame().removeAll(getPlayers());
        MiniGames.get().getGames().remove(this);
        getPlayers().forEach(playerID -> {
            resetPlayer(playerID);
            idConvert(playerID).teleport(MiniGames.get().getSpawnWorld().getSpawnLocation());
        });
        players.clear();
        setHasStarted(false);
        setActive(false);
        MiniGames.get().createGames();
    }

    public void setAllPlayersToSurvival(){
        getPlayers().forEach(player -> idConvert(player).setGameMode(GameMode.SURVIVAL));
    }


    public void addPlayer(@NotNull Player player, @NotNull String txt){
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

    public void resetPlayer(@NotNull UUID player){
        Player p = idConvert(player);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.setHealth(20);
        p.setFoodLevel(20);
    }

    public void removePlayer(@NotNull UUID player) {
        this.getPlayers().remove(player);
        MiniGames.get().getPlayersInGame().remove(player);
        if(Bukkit.getOfflinePlayer(player).isOnline()){
            resetPlayer(player);
            idConvert(player).teleport(MiniGames.get().getSpawnWorld().getSpawnLocation());
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

}
