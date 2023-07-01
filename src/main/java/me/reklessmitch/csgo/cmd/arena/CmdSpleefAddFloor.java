package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.colls.ArenaColl;
import me.reklessmitch.csgo.configs.Arena;
import org.bukkit.ChatColor;

public class CmdSpleefAddFloor extends ArenaCommand {

    public CmdSpleefAddFloor(){
        this.addAliases("addfloor");
        this.addParameter(TypeString.get(), "arenaName");
        this.addParameter(TypeString.get(), "add/remove");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        Arena arena = ArenaColl.get().get(this.readArg());
        String type = this.readArg();
        if(arena == null || type == null){
            msg("Invalid format /arena addfloor (arenaName) (add/remove)");
            return;
        }
        switch(type){
            case "add" -> arena.addFloor(me.getLocation());
            case "remove" -> arena.addRemoveFloor(me.getLocation());
            default -> {
                msg("Invalid argument, use add or remove");
                return;
            }
        }
        msg(ChatColor.GREEN + "Floor added!");
        arena.changed();
    }
}
