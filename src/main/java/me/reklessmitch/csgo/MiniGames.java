package me.reklessmitch.csgo;

import com.massivecraft.massivecore.MassivePlugin;
import lombok.Getter;
import me.reklessmitch.csgo.cmd.arena.CmdArena;
import me.reklessmitch.csgo.cmd.other.CmdCSGOShop;
import me.reklessmitch.csgo.cmd.other.CmdCreateGames;
import me.reklessmitch.csgo.cmd.other.CmdGamesGUI;
import me.reklessmitch.csgo.cmd.kits.CmdKit;
import me.reklessmitch.csgo.colls.*;
import me.reklessmitch.csgo.configs.SpleefArena;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.games.ffa.Spleef;
import me.reklessmitch.csgo.games.tpg.FlowerPower;
import org.bukkit.Bukkit;

import java.util.*;

public final class MiniGames extends MassivePlugin {

    private static MiniGames i;
    public static MiniGames get() { return i; }

    @Getter List<Game> games = new ArrayList<>();

    int gameAmount = 0;

    public MiniGames() {
        this.setVersionSynchronized(true);
        MiniGames.i = this;
    }


    @Override
    public void onEnableInner(){
        i = this;
        this.activate(
                MConfColl.class,
                KitColl.class,
                FlowerPowerArenaColl.class,
                SpleefArenaColl.class,
                FFAArenaColl.class,
                // Cmds
                CmdArena.class,
                CmdKit.class,
                CmdCreateGames.class,
                CmdGamesGUI.class,
                CmdCSGOShop.class,
                // Arena
                // Events
                Game.class
        );

        // Every 10 minutes create more games.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::createGames, 0L, 12000L);
    }

    public void createGames(){
        FlowerPowerArenaColl.get().getAll().forEach(arena -> {
            if (!arena.isActive()) {
                FlowerPower game = new FlowerPower(arena);
                games.add(game);
            }
        });
        SpleefArenaColl.get().getAll().forEach(arena -> {
            if (!arena.isActive()) {
                Spleef game = new Spleef(arena);
                games.add(game);
            }
        });
    }


    @Override
    public void onDisable(){
        super.onDisable();
        games.forEach(game -> game.setActive(false));
        i = null;
    }

    public int getNewGameID() {
        return gameAmount++;
    }
}
