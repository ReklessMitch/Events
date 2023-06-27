package me.reklessmitch.csgo;

import com.massivecraft.massivecore.MassivePlugin;
import lombok.Getter;
import me.reklessmitch.csgo.cmd.arena.CmdArena;
import me.reklessmitch.csgo.cmd.other.*;
import me.reklessmitch.csgo.cmd.kits.CmdKit;
import me.reklessmitch.csgo.colls.*;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.games.ffa.OIAC;
import me.reklessmitch.csgo.games.other.Parkour;
import me.reklessmitch.csgo.games.other.Spleef;
import me.reklessmitch.csgo.games.tg.CSGO;
import me.reklessmitch.csgo.games.todo.BattleRoyale;
import me.reklessmitch.csgo.games.tpg.FlowerPoker;
import me.reklessmitch.csgo.utils.NameTagHider;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.*;

@Getter
public final class MiniGames extends MassivePlugin {

    private static MiniGames i;
    NameTagHider nameTagHider;
    public static MiniGames get() { return i; }

    List<Game> games = new ArrayList<>();
    Set<UUID> playersInGame = new HashSet<>();
    World eventWorld;
    World spawnWorld;
    World brWorld;
    int gameAmount = 0;

    public MiniGames() {
        this.setVersionSynchronized(true);
        MiniGames.i = this;
    }


    @Override
    public void onEnableInner(){
        i = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, i);
        nameTagHider = new NameTagHider(this);
        this.activate(
            MConfColl.class,
            KitColl.class,
            FlowerPowerArenaColl.class,
            SpleefArenaColl.class,
            FFAArenaColl.class,
            TeamArenaColl.class,
            ParkourArenaColl.class,
            BRArenaColl.class,
            // Cmds
            CmdArena.class,
            CmdKit.class,
            CmdSpawn.class,
            CmdCreateGames.class,
            CmdGamesGUI.class,
            CmdCSGOShop.class,
            CmdGetCustomData.class,
            // Arena
            Game.class
        );

        // Every 10 minutes create more games.
        eventWorld = Bukkit.getWorld(MConf.get().getEventWorld());
        spawnWorld = Bukkit.getWorld(MConf.get().getSpawnWorld());
        endAllArenas();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::createGames, 0L, 12000L);
    }

    public void createGames(){
        if(MConf.get().isFlowerPowerEnabled()) {
            FlowerPowerArenaColl.get().getAll().forEach(arena -> {
                if (!arena.isActive()) {
                    games.add(new FlowerPoker(arena));
                }
            });
        }
        if(MConf.get().isSpleefEnabled()) {
            SpleefArenaColl.get().getAll().forEach(arena -> {
                if (!arena.isActive()) {
                    games.add(new Spleef(arena));
                }
            });
        }
        if(MConf.get().isFfaEnabled()) {
            FFAArenaColl.get().getAll().forEach(arena -> {
                if (!arena.isActive()) {
                    games.add(new OIAC(arena));
                }
            });
        }
        TeamArenaColl.get().getAll().forEach(arena -> {
            if(!arena.isActive()){
                games.add(new CSGO(arena));
            }
        });

        ParkourArenaColl.get().getAll().forEach(arena -> {
            if(!arena.isActive()){
                games.add(new Parkour(arena));
            }
        });

        BRArenaColl.get().getAll().forEach(arena -> {
            if(!arena.isActive()){
                games.add(new BattleRoyale(arena));
            }
        });
    }

    private void endAllArenas(){
        SpleefArenaColl.get().getAll().forEach(arena -> {
            arena.setActive(false);
            arena.changed();
        });
        FlowerPowerArenaColl.get().getAll().forEach(arena -> {
            arena.setActive(false);
            arena.changed();
        });
        FFAArenaColl.get().getAll().forEach(arena -> {
            arena.setActive(false);
            arena.changed();
        });
        TeamArenaColl.get().getAll().forEach(arena -> {
            arena.setActive(false);
            arena.changed();
        });
        ParkourArenaColl.get().getAll().forEach(arena -> {
            arena.setActive(false);
            arena.changed();
        });
        BRArenaColl.get().getAll().forEach(arena -> {
            arena.setActive(false);
            arena.changed();
        });
    }

    @Override
    public void onDisable(){
        i = null;
        super.onDisable();
    }

    public int getNewGameID() {
        return gameAmount++;
    }
}
