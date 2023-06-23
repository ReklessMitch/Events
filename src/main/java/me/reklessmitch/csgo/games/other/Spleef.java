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
        new Countdown(15)
                .onTick(tick -> {
                    String titleText = ChatColor.GREEN + "Cooldown: " + tick;
                    String subtitleText = ChatColor.GRAY + "Get ready!";
                    getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(player, 0, 20, 0, titleText, subtitleText));})
                .onComplete(() -> setupFloors(arena.getRemoveFloor(), Material.AIR))
                .start(MiniGames.get());
    }

    private void setUpGame(){
        super.start();
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
        if(this.getPlayers().size() >= 2){
            if(isStarting()) return;
            doStartCountDown();
            Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        }
    }

    private void doStartCountDown() {
        setStarting(true);
        new Countdown(20)
                .onTick(tick -> {
                    if(!isStarting()) return;
                    String titleText = ChatColor.GREEN + "Spleef Starting in: " + tick;
                    String subtitleText = ChatColor.GRAY + "Get ready!";
                    getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(player, 0, 20, 0, titleText, subtitleText));})
                .onComplete(() -> {
                    getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(player, 0, 20, 0, ChatColor.GREEN + "GO!", ChatColor.GRAY + "Good luck!"));
                    setUpGame();
                })
                .start(MiniGames.get());
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
    public void onBreak(BlockBreakEvent e){
        if(!this.getPlayers().contains(e.getPlayer().getUniqueId())) return;
        if(e.getBlock().getType().equals(SNOW_BLOCK)){
            e.setCancelled(true);
            e.getPlayer().getInventory().addItem(snowBall.asQuantity(8));
            e.getBlock().setType(Material.AIR);
        }
    }
}
