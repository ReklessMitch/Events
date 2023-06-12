package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.colls.FFAArenaColl;
import me.reklessmitch.csgo.colls.FlowerPowerArenaColl;
import me.reklessmitch.csgo.colls.SpleefArenaColl;

public class CmdArenaCreate extends ArenaCommand {

    public CmdArenaCreate(){
        this.addAliases("create");
        this.addParameter(TypeString.get(), "arenaType", "FFA");
        this.addParameter(TypeString.get(), "arenaname", "dusk");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        String arenaName = this.readArg();
        String arenaType = this.readArg();
        switch(arenaType){
            case "FFA" -> FFAArenaColl.get().create(arenaName);
            case "FP" -> FlowerPowerArenaColl.get().create(arenaName);
            case "SPLEEF" -> SpleefArenaColl.get().create(arenaName);
            default -> this.msg("Invalid arena type");
        }
        this.msg("Arena " + arenaName + " created!");
    }
}
