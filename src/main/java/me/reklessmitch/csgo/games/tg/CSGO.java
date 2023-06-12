package me.reklessmitch.csgo.games.tg;

import me.reklessmitch.csgo.configs.TeamArena;
import me.reklessmitch.csgo.games.TeamGame;

public class CSGO extends TeamGame {
    TeamArena arena;
    int teamSize;
    int rounds = 12;

    int teamAScore = 0;
    int teamBScore = 0;

    int purchaseTime = 20; // time to freeze the player


    public CSGO(TeamArena arena) {
        super();
        this.arena = arena;
        arena.setActive(true);
    }

    @Override
    public void start() {
        if(teamSize * 2 == getPlayers().size()) {
            // clearInventories, set health, set hunger
            // put players onto teams and tp them to spawnpoints
            // freeze players for purchaseTime seconds (give them a countdown)
            // open the gui to buy items
            // when purchaseTime is up, unfreeze players, close gui
            // start the round
            // repeat until rounds is up
            // swap sides after rounds / 2
        }
    }

}
