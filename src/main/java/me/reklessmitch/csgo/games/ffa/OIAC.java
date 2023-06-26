package me.reklessmitch.csgo.games.ffa;

import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.FFAArena;
import me.reklessmitch.csgo.utils.DisplayItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class OIAC extends FFAGame {

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
        setMaxPlayers(20);
    }

    @Override
    public void startGame(){
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        super.startGame();
    }


    @EventHandler
    public void onBowShot(ProjectileHitEvent event){
        if(event.getEntity().getShooter() instanceof Player player && event.getEntity() instanceof Arrow){
            if(event.getHitEntity() instanceof Player hitPlayer){
                hitPlayer.damage(1000);
            }
            player.getInventory().addItem(new ItemStack(Material.ARROW));
        }
    }
}
