package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.colls.SpleefArenaColl;
import me.reklessmitch.csgo.configs.SpleefArena;

public class CmdSpleefAddFloor extends ArenaCommand {

    public CmdSpleefAddFloor(){
        this.addAliases("addfloor");
        this.addParameter(TypeString.get(), "arenaName");
        this.addParameter(TypeString.get(), "add/remove");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        SpleefArena arena = SpleefArenaColl.get().get(this.readArg());
        String type = this.readArg();
        switch(type){
            case "add" -> arena.addFloor(me.getLocation());
            case "remove" -> arena.addRemoveFloor(me.getLocation());
            default -> {
                msg("Invalid argument, use add or remove");
                return;
            }
        }
        msg("Floor added!");
        arena.changed();
    }
}
