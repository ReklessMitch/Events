package me.reklessmitch.csgo.torny;

import lombok.Getter;
import me.reklessmitch.csgo.games.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
@Getter
public class GameEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UUID winner;
    private final Game game;

    public GameEndEvent(UUID winner, Game game) {
        this.winner = winner;
        this.game = game;
    }


    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
