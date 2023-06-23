package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.colls.ParkourArenaColl;
import me.reklessmitch.csgo.utils.SerLocation;

public class CmdArenaParkourAddCheckPoint extends ArenaCommand {

    public CmdArenaParkourAddCheckPoint(){
        this.addAliases("addparkourcheckpoint");
        this.addParameter(TypeString.get(), "arenaname", "parkour1");

    }

    @Override
    public void perform() throws MassiveException {
        ParkourArenaColl.get().get(this.readArg()).getCheckPoints().add(new SerLocation(me.getLocation()));

    }
}
