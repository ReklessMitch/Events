package me.reklessmitch.csgo.cmd.kits;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.configs.Kit;
import me.reklessmitch.csgo.colls.KitColl;
import org.bukkit.entity.Player;

public class CmdKitCreate extends ArenaCommand {

    public CmdKitCreate() {
        this.addAliases("create");
        this.addParameter(TypeString.get(), "kit");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        String kit = this.readArg();
        if(kit == null){
            this.msg("Kit name cannot be null");
            return;
        }
        Kit createdKit = KitColl.get().create(kit);
        createdKit.setInventory(((Player) sender).getInventory().getContents());
    }
}
