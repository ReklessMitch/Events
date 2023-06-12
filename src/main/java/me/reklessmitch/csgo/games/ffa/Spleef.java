package me.reklessmitch.csgo.games.ffa;

import com.massivecraft.massivecore.util.ItemBuilder;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.games.FFAGame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class Spleef extends FFAGame {

    ItemStack shovel;
    ItemStack snowBall;

    // Needs Spleef arenas?
    // - SpleefArenaColl.java
    // - SpleefArena.java
    // - List<Cubiod> spleefFloors
    // - SpawnLocation


    public Spleef () {
        super();
        shovel = new ItemBuilder(Material.DIAMOND_SHOVEL, MConf.get().getSpleefShovelName()).build();
        snowBall = new ItemBuilder(Material.SNOWBALL, MConf.get().getSpleefSnowballName()).build();
    }

    @Override
    public void start() {
        this.getPlayers().forEach(player -> {
            player.getInventory().clear();
            player.getInventory().addItem(shovel);
            player.getInventory().addItem(snowBall.asQuantity(16));
            player.getInventory();
        });
        // Teleport to start location
        // Drop the floor after X seconds

    }

    @EventHandler(ignoreCancelled = true)
    public void snowBallThrow(ProjectileHitEvent e) {
        if (e.getEntity().getShooter() instanceof Player player && this.getPlayers().contains(player) && e.getEntity() instanceof Snowball && e.getHitBlock() != null && e.getHitBlock().getType().equals(Material.SNOW_BLOCK)) {
            e.getHitBlock().setType(Material.AIR);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e){
        if(!this.getPlayers().contains(e.getPlayer())) return;
        if(e.getBlock().getType().equals(Material.SNOW_BLOCK)){
            e.setCancelled(true);
            e.getPlayer().getInventory().addItem(snowBall.asQuantity(8));
            e.getBlock().setType(Material.AIR);
        }
    }
}
