package me.reklessmitch.csgo.games.other;

import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.ItemBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import it.endlessgames.voidteleport.api.VoidTeleportEvent;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.configs.SpleefArena;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.DisplayItem;
import me.reklessmitch.csgo.utils.SerLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.bukkit.Material.BLUE_STAINED_GLASS;
import static org.bukkit.Material.SNOW_BLOCK;

public class Spleef extends Game {

    private final ItemStack shovel;
    private final ItemStack snowBall;
    private final SpleefArena arena;

    public Spleef (SpleefArena arena){
        super();
        this.arena = arena;
        arena.setActive(true);
        arena.changed();
        setDisplayItem(new DisplayItem(
                Material.SNOW_BLOCK,
                "&c&lSPLEEF - " + arena.getName(),
                List.of("&7FFA Spleef"),
                0
        ));

        shovel = new ItemBuilder(Material.DIAMOND_SHOVEL, ChatColor.translateAlternateColorCodes('&', MConf.get().getSpleefShovelName())).build();
        snowBall = new ItemBuilder(Material.SNOWBALL, ChatColor.translateAlternateColorCodes('&', MConf.get().getSpleefSnowballName())).build();
    }

    @EventHandler
    public void voidTPEvent(VoidTeleportEvent event) {
        super.voidEvent(event);
    }

    @Override
    public void end() {
        super.end();
        Optional<UUID> player = this.getPlayers().stream().findAny();
        player.ifPresent(value -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                "&b&l" + Bukkit.getPlayer(value).getName() + " &fwon the game!")));
        arena.setActive(false);
        this.setStarting(false);
    }


    private void doCountdown(){
        new Countdown(15).onTick(tick ->
                        getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 20, 0,
                                "&aSpleef starting in: " + tick, "&7&lGet ready!")))
                .onComplete(() -> {
                    setHasStarted(true);
                    setupFloors(arena.getRemoveFloor(), Material.AIR);
                }).start(MiniGames.get());
    }

    private void startGame(){
        super.start();
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        setupFloors(arena.getRemoveFloor(), BLUE_STAINED_GLASS);
        setupFloors(arena.getFloors(), Material.SNOW_BLOCK);
        this.getPlayers().forEach(player -> {
            arena.getSpawnLocation().teleport(player);
            Player p = Bukkit.getPlayer(player);
            p.getInventory().clear();
            p.getInventory().addItem(shovel);
            p.getInventory().addItem(snowBall.asQuantity(16));
        });

        doCountdown();
    }

    @Override
    public void removePlayer(UUID player) {
        super.removePlayer(player);
        if(!isActive() && isStarting()) {
            if(getPlayers().size() < 2) {
                setStarting(false);
            }
        }
        if(isActive() && getPlayers().size() < 2) {
            end();
        }
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


    private void setupFloors(List<SerLocation> floors, Material removeFloor) {
        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(arena.getSpawnLocation().getLocation().getWorld()))) {
            floors.forEach(serLocation -> {
                session.makeCylinder(serLocation.getVector3(), BukkitAdapter.adapt(removeFloor.createBlockData()), arena.getFloorRadius(), 1, true);
                session.makeHollowCylinder(serLocation.getVector3(), BukkitAdapter.adapt(Material.BEDROCK.createBlockData()), arena.getFloorRadius(), arena.getFloorRadius(),  1, 0);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void snowBallThrow(ProjectileHitEvent e) {
        if (e.getEntity().getShooter() instanceof Player player && this.getPlayers().contains(player.getUniqueId()) && e.getEntity() instanceof Snowball && e.getHitBlock() != null && e.getHitBlock().getType().equals(SNOW_BLOCK)) {
            e.getHitBlock().setType(Material.AIR);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event){
        Player player = (Player) event.getWhoClicked();
        if(!this.getPlayers().contains(player.getUniqueId())) return;
        if(event.getRecipe().getResult().getType().equals(SNOW_BLOCK)){
            event.setCancelled(true);
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

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e){
        if(!this.getPlayers().contains(e.getPlayer().getUniqueId())) return;
        if(e.getBlock().getType().equals(SNOW_BLOCK)){
            e.setCancelled(true);
            e.getPlayer().getInventory().addItem(snowBall.asQuantity(8));
            e.getBlock().setType(Material.AIR);
        }
    }
}
