package me.reklessmitch.csgo.cmd.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdArenaJoin extends ArenaCommand {

    public CmdArenaJoin() {
        this.addAliases("join");
        this.addParameter(TypeInteger.get(), "gameID", "1");
        this.addRequirements(RequirementHasPerm.get(Perm.JOIN));
    }

    @Override
    public void perform() throws MassiveException {
        int gameID = this.readArg();
        Player player = (Player) sender;
        // Get the arena from games map in CSGO.java
        Game game = MiniGames.get().getGames().stream().filter(g -> g.getGameID() == gameID).findFirst().orElse(null);
        if (game == null) {
            this.msg("Game with ID " + gameID + " does not exist");
            return;
        }
        game.addPlayer(player);
    }
}
