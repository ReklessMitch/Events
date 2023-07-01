package me.reklessmitch.csgo.cmd.other;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.Perm;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.colls.ArenaColl;
import me.reklessmitch.csgo.colls.KitColl;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.configs.Kit;
import me.reklessmitch.csgo.torny.Tournament;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class CmdCreateTorny extends ArenaCommand {
    private static final CmdCreateTorny i = new CmdCreateTorny();
    public static CmdCreateTorny get() { return i; }

    public CmdCreateTorny() {
        this.addAliases("createTorny");
        this.addParameter(TypeString.get(), "type");
        this.addParameter(TypeInteger.get(), "playersAmount");
        this.addRequirements(RequirementHasPerm.get(Perm.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        String type = this.readArg();
        int teamsAmount = this.readArg();

        // Add torny creation here
        List<Arena> arenas = new ArrayList<>();
        ArenaColl.get().getAll().forEach(arena -> {
            if (arena.isActive()) return;
            List<String> allowedGames = arena.getAllowedGames();
            if (allowedGames.isEmpty()) return;
            Bukkit.broadcastMessage(allowedGames.toString());
            if (allowedGames.contains(type)) {
                arenas.add(arena);
            }
        });
        if(arenas.size() >= teamsAmount / 2){
            Kit kit = KitColl.get().get(type);
            Tournament tournament = new Tournament(arenas, kit, teamsAmount);
            MiniGames.get().getTournaments().add(tournament);
            me.sendMessage("Tournament created");
        }
    }
}
