package me.reklessmitch.csgo.games.other;

import com.massivecraft.massivecore.mixin.MixinTitle;
import it.endlessgames.voidteleport.api.VoidTeleportEvent;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.ParkourArena;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.DisplayItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class Parkour extends Game {

    ParkourArena arena;
    Map<UUID, List<Location>> checkpoints;
    Location startLocation;
    List<UUID> finished = new ArrayList<>();
    int maxWinners = 2;

    public Parkour(ParkourArena arena) {
        super();
        this.arena = arena;
        arena.setActive(true);
        arena.changed();
        setMaxPlayers(20);
        setDisplayItem(new DisplayItem(
                Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
                "&c&lPARKOUR - " + arena.getName(),
                List.of("&7Race to the end!", "", "&7Winners: " + maxWinners),
                0
        ));
        this.checkpoints = new HashMap<>();
        this.startLocation = arena.getStartLocation().getLocation();
    }

    private void startGame(){
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        // if its not but its above 2 then start a timer to start the game

        super.start();
        getPlayers().forEach(p -> {
            checkpoints.put(p, new ArrayList<>());
            Bukkit.getPlayer(p).teleport(startLocation);
            resetPlayer(p);
        });
        doCountdown();
    }

    @Override
    public void start() {
        if (getPlayers().size() >= getMinPlayers() && !isStarting()){
            setStarting(true);
            new Countdown(30).onTick(tick -> {
                if(tick % 5 == 0 || tick <= 5){
                    getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 20, 0, "&7Game starting in...", "&c&l" + tick));
                }
            }).onComplete(() -> {
                if(getPlayers().size() >= getMinPlayers()) startGame();
                else {
                    setStarting(false);
                    getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 20, 0, "&c&lNot enough players!", "&7Game cancelled!"));
                }
            }).start(MiniGames.get());
        }
    }

    @Override
    public void end() {
        super.end();
        arena.setActive(false);
        arena.changed();
        StringBuilder message = new StringBuilder("&b&l -- PARKOUR RESULTS -- \n");
        for(int i = 0; i < maxWinners; i++){
            message.append("&3&l").append(i + 1).append(": &e").append(Bukkit.getOfflinePlayer(finished.get(i)).getName()).append("\n");
        }
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message.toString()));
        getPlayers().forEach(this::removePlayer);

    }

    private void teleportToCheckpoint(Player player){
        List<Location> playerCheckPoints = checkpoints.get(player.getUniqueId());
        if(playerCheckPoints.isEmpty()) player.teleport(startLocation);
        else player.teleport(playerCheckPoints.get(playerCheckPoints.size()-1));
    }

    @EventHandler(ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player player) || !getPlayers().contains(player.getUniqueId())) return;
        if(e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            e.setCancelled(true);
            teleportToCheckpoint((Player) e.getEntity());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void voidTPEvent(VoidTeleportEvent event){
        if(!getPlayers().contains(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);
        teleportToCheckpoint(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event){
        UUID playerID = event.getPlayer().getUniqueId();
        if(!getPlayers().contains(playerID)) return;
        if(!isHasStarted()){
            event.setCancelled(true);
            return;
        }
        // get block below the player
        Location blockBelow = event.getTo().clone().subtract(0, 1, 0);
        Block block = blockBelow.getBlock();
        if(block.getType().equals(Material.GOLD_BLOCK)){
            if(checkpoints.get(playerID).contains(block.getLocation().add(0,1,0))) return;
            event.getPlayer().sendMessage(ChatColor.GREEN + "Checkpoint reached!");
            checkpoints.get(playerID).add(block.getLocation().add(0,1,0));
        }
        if(block.getType().equals(Material.IRON_BLOCK)){
            if(finished.contains(playerID)) return;
            finished.add(playerID);
            getPlayers().forEach(p -> Bukkit.getPlayer(p).sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&l" + event.getPlayer().getName() + "&a finished the parkour in position: &c&l" + finished.size())));
            if(finished.size() == maxWinners) end();
        }
    }

    private void doCountdown(){
        new Countdown(15).onTick(tick ->
                getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 20, 0,
                        "&aParkour starting in: " + tick, "&7&lGet ready!")))
        .onComplete(() -> setHasStarted(true)).start(MiniGames.get());
    }


}
