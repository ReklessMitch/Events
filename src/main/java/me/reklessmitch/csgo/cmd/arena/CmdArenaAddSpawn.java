package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.configs.FFAArena;

public class CmdArenaAddSpawn extends ArenaCommand {


    public CmdArenaAddSpawn() {
        this.addAliases("setspawn");
        this.addParameter(TypeString.get(), "arenaname", "dusk");
        this.addParameter(TypeString.get(), "team", "team1");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        String arenaName = this.readArg();
        String team = this.readArg();
        if(team == null){
            this.msg("Team cannot be null");
            return;
        }
    }
}

