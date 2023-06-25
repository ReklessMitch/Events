package me.reklessmitch.csgo.games.ffa;

import com.massivecraft.massivecore.util.ItemBuilder;
import com.massivecraft.massivecore.util.MUtil;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.FFAArena;
import me.reklessmitch.csgo.games.FFAGame;
import me.reklessmitch.csgo.guis.SelectKitGUI;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.DisplayItem;
import net.royawesome.jlibnoise.module.combiner.Select;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class OIAC extends FFAGame {

    private final ItemStack bow;
    private final ItemStack arrow;
    int scoreToWin = 10;

    public OIAC(FFAArena arena) {
        super(arena);
        arena.setActive(true);
        arena.changed();
        setDisplayItem(new DisplayItem(
                Material.BOW,
                "&c&lOIAC - " + arena.getName(),
                List.of("&7FFA Gamemode", "&7One in the chamber"),
                0
        ));
        setMaxPlayers(2);
        bow = new ItemBuilder(Material.BOW, "&4&lOIAC Bow").build();
        arrow = new ItemBuilder(Material.ARROW, "&c&lOIAC Arrow").build();
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
    }

    private void reset(UUID playerID){
        Player player = Bukkit.getPlayer(playerID);
        player.getInventory().clear();
        player.getInventory().addItem(bow, arrow);
    }

    private void startGame(){
        super.start();
        new Countdown(10)
            .onTick(tick -> getPlayers().forEach(player ->
                    Bukkit.getPlayer(player).sendTitle("Starting in", String.valueOf(tick))))
            .onComplete(() -> getPlayers().forEach(player -> {
                reset(player);
                getKills().put(player, 0);
                setStarting(true);
            })).start(MiniGames.get());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID playerID = event.getPlayer().getUniqueId();
        if (!getPlayers().contains(playerID)) return;
        if (!isStarting()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRespawnEvent(PlayerRespawnEvent event){
        if(!getPlayers().contains(event.getPlayer().getUniqueId())) return;
        event.setRespawnLocation(MUtil.random(getArena().getSpawnLocations()).getLocation());
        reset(event.getPlayer().getUniqueId());
    }

    @Override
    public void start() {
        if(getPlayers().size() < getMaxPlayers() || isStarting()){ return; }
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), this::startGame, 100L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!getPlayers().contains(event.getPlayer().getUniqueId()) || event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        getKills().put(killer.getUniqueId(), getKills().get(killer.getUniqueId()) + 1);
        if(getKills().get(killer.getUniqueId()) >= scoreToWin){
            getPlayers().forEach(player -> Bukkit.getPlayer(player).sendTitle("Game Over", killer.getName() + " won!"));
            end();
        }
    }

    @EventHandler
    public void onBowShot(ProjectileHitEvent event){
        if(event.getEntity().getShooter() instanceof Player player && event.getEntity() instanceof Arrow){
            if(event.getHitEntity() instanceof Player hitPlayer){
                hitPlayer.damage(1000);
            }
            player.getInventory().addItem(arrow);
        }
    }
}
