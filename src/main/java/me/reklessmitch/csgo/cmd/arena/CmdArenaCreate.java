package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.colls.*;
import me.reklessmitch.csgo.configs.*;

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
        switch(arenaType.toLowerCase()){
            case "ffa" -> {
                FFAArena arena = FFAArenaColl.get().create(arenaName);
                arena.setName(arenaName);
            }
            case "fp" -> {
                FlowerPowerArena arena = FlowerPowerArenaColl.get().create(arenaName);
                arena.setName(arenaName);
            }
            case "spleef" -> {
                SpleefArena arena = SpleefArenaColl.get().create(arenaName);
                arena.setName(arenaName);
            }
            case "tg" -> {
                TeamArena arena = TeamArenaColl.get().create(arenaName);
                arena.setName(arenaName);
            }
            case "parkour" -> {
                ParkourArena arena = ParkourArenaColl.get().create(arenaName);
                arena.setName(arenaName);
            }
            case "br" -> {
                BRArena arena = BRArenaColl.get().create(arenaName);
                arena.setArenaName(arenaName);
            }
            default -> {
                this.msg("Invalid arena type");
                return;
            }
        }
        this.msg("Arena " + arenaName + " created!");
    }
}
