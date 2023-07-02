package me.reklessmitch.csgo;

import com.massivecraft.massivecore.MassivePlugin;
import com.massivecraft.massivecore.util.MUtil;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import lombok.Getter;
import me.reklessmitch.csgo.cmd.arena.CmdArena;
import me.reklessmitch.csgo.cmd.arena.CmdTest;
import me.reklessmitch.csgo.cmd.other.*;
import me.reklessmitch.csgo.cmd.kits.CmdKit;
import me.reklessmitch.csgo.colls.*;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.games.ffa.FFA;
import me.reklessmitch.csgo.games.other.Parkour;
import me.reklessmitch.csgo.games.other.Spleef;
import me.reklessmitch.csgo.games.tg.CSGO;
import me.reklessmitch.csgo.games.todo.BattleRoyale;
import me.reklessmitch.csgo.games.tpg.FlowerPoker;
import me.reklessmitch.csgo.torny.Tournament;
import me.reklessmitch.csgo.utils.BDGen;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;


import java.util.*;

@Getter
public final class MiniGames extends MassivePlugin {

    private static MiniGames i;
    public static MiniGames get() { return i; }

    private final List<Game> games = new ArrayList<>();
    private final List<Tournament> tournaments = new ArrayList<>();
    private final Set<UUID> playersInGame = new HashSet<>();
    private World eventWorld;
    private World spawnWorld;
    private int gameAmount = 0;

    public MiniGames() {
        this.setVersionSynchronized(true);
        MiniGames.i = this;
    }

    private void deleteBRWorld(){
        MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        MVWorldManager mvwm = core.getMVWorldManager();
        mvwm.deleteWorld("br");
    }

    private void createBRWorld(){
        WorldCreator worldCreator = new WorldCreator("br");
        worldCreator.generator(new BDGen());
        worldCreator.generateStructures(false);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.createWorld();
        worldCreator.generator(new BDGen().toString());
    }


    @Override
    public void onEnableInner(){
        i = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, i);
        this.activate(
            MConfColl.class,
            KitColl.class,
            ArenaColl.class,

            // Cmds
            CmdArena.class,
            CmdKit.class,
            CmdSpawn.class,
            CmdCreateGames.class,
            CmdTest.class,
            CmdCreateTorny.class,
            CmdTornyGUI.class,
            CmdGamesGUI.class,
            CmdCSGOShop.class,
            // Arena
            Game.class
        );
        deleteBRWorld();
        createBRWorld();
        // Every 10 minutes create more games.
        eventWorld = Bukkit.getWorld(MConf.get().getEventWorld());
        spawnWorld = Bukkit.getWorld(MConf.get().getSpawnWorld());
        endAllArenas();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::createGames, 0L, 12000L);
    }

    public void createGames(){
        ArenaColl.get().getAll().forEach(arena -> {
            if(arena.isActive()) return;
            List<String> allowedGames = arena.getAllowedGames();
            if(allowedGames.isEmpty()) return;
            String gameChosen = MUtil.random(allowedGames);
            if(gameChosen == null) return;
            Game game;
            switch(gameChosen){
                case "spleef" -> game = new Spleef(arena);
                case "flowerpoker" -> game = new FlowerPoker(arena);
                case "ffa" -> game = new FFA(arena);
                case "csgo" -> game = new CSGO(arena);
                case "parkour" -> game = new Parkour(arena);
                case "br" -> game = new BattleRoyale(arena);
                //case "mc" -> game = new MarioKart(arena);
                default -> game = null;
            }
            if(game == null) return;
            games.add(game);
        });
    }

    private void endAllArenas(){
        ArenaColl.get().getAll().forEach(arena -> {
            arena.setActive(false);
            arena.changed();
        });
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        return new BDGen();
    }

    @Override
    public void onDisable(){
        i = null;
        deleteBRWorld();
        super.onDisable();
    }

    public int getNewGameID() {
        return gameAmount++;
    }

}
