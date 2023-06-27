package me.reklessmitch.csgo.games.todo;

import com.massivecraft.massivecore.mixin.MixinTitle;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.DisplayItem;
import me.reklessmitch.csgo.utils.TeleportUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class BattleRoyale extends Game {

    Arena arena;
    int startBorderSize = 250;
    WorldBorder border;
    int reduceBorderEveryXMins = 10;
    int reduceBorderAmount = 50;
    int gracePeriod = 10;
    boolean gracePeriodActive = true;

    public BattleRoyale(Arena arena){
        super();
        this.arena = arena;
        arena.setActive(true);
        arena.changed();
        setDisplayItem(new DisplayItem(
                Material.DIAMOND_SWORD,
                "&c&lBattle Arena",
                List.of("&7FFA Battle Arena"),
                0
        ));
        setMaxPlayers(50);
    }

    @Override
    public void end(){
        border.reset();
        arena.setActive(false);
        arena.changed();
        super.end();
    }

    @Override
    public void startGame() {
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        setupWorld();
        sortPlayersInventories();
        teleportToSpawns();
    }

    private void setupWorld(){
        World world = Bukkit.getWorld(MConf.get().getBrWorld());
        border = world.getWorldBorder();
        border.setCenter(arena.getSpawnPoint().getLocation());
        border.setSize(startBorderSize);
        world.setTime(1000);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setDifficulty(Difficulty.NORMAL);
    }

    private void sortPlayersInventories() {
        uuidToPlayer(getPlayers()).forEach(player -> {
            resetPlayer(player.getUniqueId());
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16), new ItemStack(Material.TORCH, 32));
        });
    }

    private void teleportToSpawns() {
        int radius = (startBorderSize / 2) - 20;
        TeleportUtils.spawnPlayersInRadius(arena.getSpawnPoint().getLocation(), radius, uuidToPlayer(getPlayers()));
        doStartCountDown();
    }

    private void doStartCountDown() {
        new Countdown(10).onTick(tick -> {
            if(tick % 5 == 0 || tick <= 5){
                getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(
                    player, 0, 20, 0, "&c&lGame begins in", tick + " seconds"));
            }}).onComplete(() -> {
                getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(
                    player, 0, 20, 0, "&c&lGame has begun", ""));
            setHasStarted(true);
            gracePeriodCountDown();
            borderShrinkCountDown();
        }).start(MiniGames.get());
    }

    private void gracePeriodCountDown() {
        new Countdown(gracePeriod * 60).onTick(tick -> {
            if(!isActive()) return;
            if(tick % 60 == 0){
                uuidToPlayer(getPlayers()).forEach(player ->
                        player.sendMessage(ChatColor.RED + "GRACE PERIOD ENDS IN " + tick / 60 + " MINS"));
            }}).onComplete(() -> uuidToPlayer(getPlayers()).forEach(player -> {
                player.sendMessage(ChatColor.RED + "GRACE PERIOD HAS ENDED");
                gracePeriodActive = false;
            })).start(MiniGames.get());
    }

    private void borderShrinkCountDown(){
        if(border.getSize() <= 50 || !isActive()){
            return;
        }
        new Countdown(reduceBorderEveryXMins * 60).onTick(tick -> {
            if(!isActive()) return;
            if(tick % 60 == 0){
                uuidToPlayer(getPlayers()).forEach(player ->
                        player.sendMessage(ChatColor.RED + "BORDER WILL SHRINK IN " + tick / 60 + " MINS"));
            }
        }).onComplete(() -> {
            border.setSize(border.getSize() - reduceBorderAmount, 60);
            borderShrinkCountDown();
        }).start(MiniGames.get());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(getPlayers().contains(event.getPlayer().getUniqueId())){
            removePlayer(event.getPlayer().getUniqueId());
            uuidToPlayer(getPlayers()).forEach(player -> player.sendMessage(
                    ChatColor.GREEN + event.getPlayer().getName().toUpperCase() + " has been eliminated! " + getPlayers().size() + " players remaining"));
            if(getPlayers().size() == 1){
                Bukkit.broadcastMessage(ChatColor.GREEN + "" + uuidToPlayer(getPlayers()).stream().findFirst().get().getName() + " has won the game!");
                end();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID playerID = event.getPlayer().getUniqueId();
        if (!getPlayers().contains(playerID)) return;
        if (!isHasStarted()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerAttack(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player
                && getPlayers().contains(event.getEntity().getUniqueId()) && gracePeriodActive){
            event.setCancelled(true);
            event.getDamager().sendMessage(ChatColor.RED + "You cannot attack players yet!");
        }
    }

}
