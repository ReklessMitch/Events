package me.reklessmitch.csgo.cmd.arena;

import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.games.Game;
import org.bukkit.ChatColor;

public class CmdLeave extends ArenaCommand {

    public CmdLeave() {
        this.addAliases("leave");
    }

    @Override
    public void perform() {
        for(Game game : MiniGames.get().getGames()){
            if(game.getPlayers().contains(me.getUniqueId())){
                game.removePlayer(me.getUniqueId());
                msg(ChatColor.GREEN + "You have left the game / queue");
                return;
            }
        }
    }
}
