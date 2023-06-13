package me.reklessmitch.csgo.games.ffa;

import com.massivecraft.massivecore.util.ItemBuilder;
import com.massivecraft.massivecore.util.MUtil;
import me.reklessmitch.csgo.configs.FFAArena;
import me.reklessmitch.csgo.games.FFAGame;
import me.reklessmitch.csgo.utils.Countdown;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class OIAC extends FFAGame {

    private final ItemStack bow;
    private final ItemStack arrow;

    public OIAC(FFAArena arena) {
        super(arena);
        bow = new ItemBuilder(Material.BOW, "OIAC Bow").build();
        arrow = new ItemBuilder(Material.ARROW, "OIAC Arrow").build();
    }

    @Override
    public void start() {
        this.getPlayers().forEach(player -> {
            MUtil.random(getArena().getSpawnLocations()).teleport(player);
            resetPlayer(player);
            new Countdown(10)
                .onTick(tick -> player.sendTitle("Starting in", String.valueOf(tick)))
                .onComplete(() -> {
                    player.getInventory().addItem(bow);
                    player.getInventory().addItem(arrow);
                    getKills().put(player, 0);
                });
        });
    }

    @EventHandler
    public void onBowShot(ProjectileHitEvent event){
        if(event.getEntity().getShooter() instanceof Player player && event.getEntity() instanceof Arrow){
            if(event.getHitEntity() instanceof Player hitPlayer){
                hitPlayer.damage(1000);
                this.getKills().put(player, this.getKills().get(player) + 1);
            }
            player.getInventory().addItem(arrow);
        }
    }
}
