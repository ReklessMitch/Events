package me.reklessmitch.csgo.cmd.other;

import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.torny.Tournament;

public class CmdCreateTorny extends ArenaCommand {
    private static final CmdCreateTorny i = new CmdCreateTorny();
    public static CmdCreateTorny get() { return i; }

    public CmdCreateTorny() {
        this.addAliases("createTorny");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() {
        // Add torny creation here
        new Tournament();
    }
}
