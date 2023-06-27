package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.configs.*;
import me.reklessmitch.csgo.utils.SerLocation;

public class CmdArenaAddSpawn extends ArenaCommand {


    public CmdArenaAddSpawn() {
        this.addAliases("setspawn");
        this.addParameter(TypeString.get(), "arenaname", "dusk");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        String arenaName = this.readArg();
        SerLocation location = new SerLocation(me.getLocation());
        if(arenaName == null){
            msg("Invalid Format / arena setspawn (arenaName)");
            return;
        }
        Arena arena = Arena.get(arenaName);
        if(arena == null){
            msg("Invalid arena name");
            return;
        }
        arena.addSpawnLocation(location);
        arena.changed();
    }
}

