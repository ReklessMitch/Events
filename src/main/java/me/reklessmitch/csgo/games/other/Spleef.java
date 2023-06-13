package me.reklessmitch.csgo.games.other;

import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.ItemBuilder;
import com.massivecraft.massivecore.util.MUtil;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.configs.SpleefArena;
import me.reklessmitch.csgo.games.FFAGame;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.SerLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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

        shovel = new ItemBuilder(Material.DIAMOND_SHOVEL, ChatColor.translateAlternateColorCodes('&', MConf.get().getSpleefShovelName())).build();
        snowBall = new ItemBuilder(Material.SNOWBALL, ChatColor.translateAlternateColorCodes('&', MConf.get().getSpleefSnowballName())).build();
    }


    @Override
    public void end() {
        this.getPlayers().forEach(player -> {
            player.getInventory().clear();
            player.teleport(MiniGames.get().getSpawnWorld().getSpawnLocation());
        });
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                "&b&l" + this.getPlayers().stream().findAny() + " &fwon the game!"));
        this.setActive(false);
        arena.setActive(false);
        this.getPlayers().clear();
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
        setActive(true);
        setupFloors(arena.getRemoveFloor(), BLUE_STAINED_GLASS);
        this.getPlayers().forEach(player -> {
            arena.getSerLocation().teleport(player);
            player.getInventory().clear();
            player.getInventory().addItem(shovel);
            player.getInventory().addItem(snowBall.asQuantity(16));
            player.getInventory();
        });
        setupFloors(arena.getFloors(), Material.SNOW_BLOCK);
        // Countdown from 15 then remove the floor using titles

        doCountdown();
    }

    @Override
    public void start() {
        if(this.getPlayers().size() >= 2){
            Bukkit.broadcastMessage("Game starting in 30s!");
            Bukkit.getScheduler().runTaskLater(MiniGames.get(), this::setUpGame, 600);

            Bukkit.getServer().getPluginManager().registerEvents(
                    this, MiniGames.get());
        }
    }



    private void setupFloors(List<SerLocation> floors, Material removeFloor) {
        try (EditSession session = WorldEdit.getInstance().newEditSession(
                BukkitAdapter.adapt(arena.getSerLocation().getLocation().getWorld()))) {
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
        if (e.getEntity().getShooter() instanceof Player player && this.getPlayers().contains(player) && e.getEntity() instanceof Snowball && e.getHitBlock() != null && e.getHitBlock().getType().equals(SNOW_BLOCK)) {
            e.getHitBlock().setType(Material.AIR);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e){
        if(!this.getPlayers().contains(e.getPlayer())) return;
        if(e.getBlock().getType().equals(SNOW_BLOCK)){
            e.setCancelled(true);
            e.getPlayer().getInventory().addItem(snowBall.asQuantity(8));
            e.getBlock().setType(Material.AIR);
        }
    }
}
