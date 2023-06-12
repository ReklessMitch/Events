package me.reklessmitch.csgo.cmd.other;


import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.guis.GamesGUI;
import org.bukkit.entity.Player;

public class CmdGamesGUI extends ArenaCommand {
    private static final CmdGamesGUI i = new CmdGamesGUI();
    public static CmdGamesGUI get() { return i; }

    public CmdGamesGUI() {
        this.addAliases("games");
        this.addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() {
        new GamesGUI((Player) sender);
    }
}