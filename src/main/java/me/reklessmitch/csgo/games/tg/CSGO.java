package me.reklessmitch.csgo.games.tg;

import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.MUtil;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.TeamArena;
import me.reklessmitch.csgo.games.TeamGame;
import me.reklessmitch.csgo.guis.CSGOShop;
import me.reklessmitch.csgo.utils.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CSGO extends TeamGame {
    int teamSize = 1;
    int rounds = 12;
    int teams = 2;
    int teamAScore = 0;
    int teamBScore = 0;
    int purchaseTime = 20; // time to freeze the player


    public CSGO(TeamArena arena) {
        super(arena);
        arena.setActive(true);
    }

    public void findTeamAndSpawnPoint() {
        getTeams().forEach((integer, players) -> players.forEach(player ->
                MUtil.random(getArena().getTeamSpawns().get(integer))));
    }


    public void newRound() {
        findTeamAndSpawnPoint();
        getPlayers().forEach(player -> {
            resetPlayer(player);
            CSGOShop shop = new CSGOShop(player);
            new Countdown(purchaseTime).onTick(tick -> {
                player.setWalkSpeed(0);
                String titleText = ChatColor.GREEN + "Purchase Time: " + tick;
                String subtitleText = ChatColor.GRAY + "Get ready!";
                MixinTitle.get().sendTitleMessage(player, 0, 20, 0, titleText, subtitleText);
            }).onComplete(() -> {
                shop.getInventory().close();
                player.setWalkSpeed(1);
                MixinTitle.get().sendTitleMessage(player, 0, 20, 0, ChatColor.GREEN + "GO!", "");
            });
        });
    }


    @Override
    public void start() {
        if (teamSize * 2 == getPlayers().size()) {
            newRound();
        }
    }
}