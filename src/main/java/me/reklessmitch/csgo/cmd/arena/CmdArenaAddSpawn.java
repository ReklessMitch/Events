package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.configs.FFAArena;
import me.reklessmitch.csgo.configs.FlowerPowerArena;
import me.reklessmitch.csgo.configs.ParkourArena;
import me.reklessmitch.csgo.configs.SpleefArena;
import me.reklessmitch.csgo.utils.SerLocation;

public class CmdArenaAddSpawn extends ArenaCommand {


    public CmdArenaAddSpawn() {
        this.addAliases("setspawn");
        this.addParameter(TypeString.get(), "gameType", "(Spleef, CSGO, FP)");
        this.addParameter(TypeString.get(), "arenaname", "dusk");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        String gameType = this.readArg();
        String arenaName = this.readArg();
        SerLocation location = new SerLocation(me.getLocation());
        if(gameType == null){
            msg("Invalid game type: Format / arena setspawn (gameType) (arenaName)");
            return;
        }
        switch (gameType.toLowerCase()) {
            case "ffa" -> {
                FFAArena fArena = FFAArena.get(arenaName);
                fArena.getSpawnLocations().add(location);
                fArena.changed();
            }
            case "spleef" -> {
                SpleefArena sArena = SpleefArena.get(arenaName);
                sArena.setSpawnLocation(location);
                sArena.changed();
            }
            case "fp" -> {
                FlowerPowerArena fpArena = FlowerPowerArena.get(arenaName);
                fpArena.addSpawnLocation(location);
                fpArena.changed();
            }
            case "parkour" -> {
                ParkourArena pArena = ParkourArena.get(arenaName);
                pArena.setStartLocation(location);
                pArena.changed();
            }
            default -> msg("Invalid game type - Not yet implemented!");
        }
    }
}

