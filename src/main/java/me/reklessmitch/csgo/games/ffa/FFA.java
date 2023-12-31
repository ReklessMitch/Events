package me.reklessmitch.csgo.games.ffa;

import com.massivecraft.massivecore.util.MUtil;
import lombok.Getter;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.configs.Kit;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.guis.SelectKitGUI;
import me.reklessmitch.csgo.utils.DisplayItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvert;

@Getter
public class FFA extends Game {

    private final Map<UUID, Integer> kills = new HashMap<>();
    private static final int KILLS_TO_WIN = 3;
    private final Arena arena;
    private Kit kit;

    public FFA(@NotNull Arena arena) {
        super();
        this.arena = arena;
        arena.setActive(true);
        arena.changed();
        setDisplayItem(new DisplayItem(
                Material.BOW,
                "&c&lFFA - " + arena.getName(),
                List.of("&7Last Man Standing wins!"),
                0
        ));
        setMaxPlayers(20);
    }


    @Override
    public void startGame(){
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        getPlayers().forEach(p -> MUtil.random(arena.getSpawnPoints()).teleport(p));
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID playerID = event.getPlayer().getUniqueId();
        if (!getPlayers().contains(playerID)) return;
        if (!isHasStarted()) {
            event.setCancelled(true);
        }
    }

    public void reset(@NotNull UUID playerID) {
        Player player = idConvert(playerID);
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
        event.setRespawnLocation(MUtil.random(arena.getSpawnPoints()).getLocation());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!getPlayers().contains(event.getPlayer().getUniqueId()) || event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        getKills().put(killer.getUniqueId(), getKills().get(killer.getUniqueId()) + 1);

        if(getKills().get(killer.getUniqueId()) >= KILLS_TO_WIN){
            getPlayers().forEach(player -> Bukkit.getPlayer(player).sendTitle("Game Over", killer.getName() + " won!"));
            event.setCancelled(true);
            end();
        }
    }

    @EventHandler
    public void onBowShot(ProjectileHitEvent event){
        if(!kit.getName().equals("OIAC")) return;
        if(event.getEntity().getShooter() instanceof Player player && event.getEntity() instanceof Arrow){
            if(!getPlayers().contains(player.getUniqueId())) return;
            if(event.getHitEntity() instanceof Player hitPlayer){
                hitPlayer.damage(1000);
            }
            player.getInventory().addItem(new ItemStack(Material.ARROW));
        }
    }

}
