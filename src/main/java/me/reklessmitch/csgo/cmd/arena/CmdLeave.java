package me.reklessmitch.csgo.cmd.arena;

import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.cmd.ArenaCommand;
import org.bukkit.ChatColor;

public class CmdLeave extends ArenaCommand {

    public CmdLeave() {
        this.addAliases("leave");
    }

    @Override
    public void perform() {
        MiniGames.get().getGames().forEach(game -> {
            if(game.getPlayers().contains(me.getUniqueId())){
                game.removePlayer(me.getUniqueId());
                msg(ChatColor.GREEN + "You have left the game / queue");
            }
        });
    }
}
