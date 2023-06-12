package me.reklessmitch.csgo.cmd.other;

import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;

public class CmdCreateGames extends ArenaCommand {
    private static final CmdCreateGames i = new CmdCreateGames();
    public static CmdCreateGames get() { return i; }

    public CmdCreateGames() {
        this.addAliases("creategames");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() {
        MiniGames.get().createGames();
    }
}
