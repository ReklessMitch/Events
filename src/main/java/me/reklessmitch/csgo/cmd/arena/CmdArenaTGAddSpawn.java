package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.configs.Arena;

public class CmdArenaTGAddSpawn extends ArenaCommand {

    public CmdArenaTGAddSpawn() {
        this.addAliases("settgspawn");
        this.addParameter(TypeString.get(), "arenaname", "dusk");
        this.addParameter(TypeInteger.get(), "team", "1/2");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        String arenaName = this.readArg();
        int team = this.readArg();
        if(arenaName == null || team == 0){
            msg("Invalid Format / arena settgspawn (arenaName) (team)");
            return;
        }
        if(team != 1 && team != 2){
            msg("Invalid team number");
            return;
        }
        Arena arena = Arena.get(arenaName);
        arena.addSpawnLocation(team, me.getLocation());
        arena.changed();
    }
}
