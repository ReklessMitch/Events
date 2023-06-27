package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.colls.*;
import me.reklessmitch.csgo.configs.*;
import org.bukkit.ChatColor;

public class CmdArenaCreate extends ArenaCommand {

    public CmdArenaCreate(){
        this.addAliases("create");
        this.addParameter(TypeString.get(), "arenaname", "dusk");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        String arenaName = this.readArg();
        if(arenaName == null){
            this.msg("Invalid arena name or type");
            return;
        }
        Arena arena = ArenaColl.get().create(arenaName);
        arena.setName(arenaName);
        arena.changed();
        this.msg(ChatColor.GREEN + arenaName.toUpperCase() + " created!");
    }
}
