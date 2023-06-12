package me.reklessmitch.csgo.cmd.other;

import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.guis.CSGOShop;
import org.bukkit.entity.Player;

public class CmdCSGOShop extends ArenaCommand {
    private static final CmdCSGOShop i = new CmdCSGOShop();
    public static CmdCSGOShop get() { return i; }

    public CmdCSGOShop() {
        this.addAliases("cshop");
        this.addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() {
        new CSGOShop((Player) sender);
    }
}
