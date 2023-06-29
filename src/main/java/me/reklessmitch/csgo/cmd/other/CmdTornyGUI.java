package me.reklessmitch.csgo.cmd.other;

import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.guis.GamesGUI;
import me.reklessmitch.csgo.guis.TournyGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdTornyGUI extends ArenaCommand {
    private static final CmdTornyGUI i = new CmdTornyGUI();
    public static CmdTornyGUI get() { return i; }

    public CmdTornyGUI() {
        this.addAliases("torny");
        this.addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() {
        new TournyGUI().open((Player) sender);
        Bukkit.broadcastMessage(MiniGames.get().getTournaments().size() + "tournaments active");
    }
}