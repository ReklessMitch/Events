package me.reklessmitch.csgo.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Countdown extends BukkitRunnable {
    private int currentSecond;
    private @Nullable TickAction tickAction;
    private @Nullable Runnable completeAction;

    public Countdown(int seconds) {
        this.currentSecond = seconds;
    }

    public Countdown onTick(TickAction action) {
        this.tickAction = action;
        return this;
    }

    public Countdown onComplete(Runnable action) {
        this.completeAction = action;
        return this;
    }

    @Override
    public void run() {
        // TODO: 03/07/2023 Improve this
        if (currentSecond > 0) {
            if (tickAction != null) {
                tickAction.execute(currentSecond);
            }
            currentSecond--;
        } else {
            cancel();
            if (completeAction != null) {
                completeAction.run();
            }
        }
    }

    public void start(@NotNull JavaPlugin plugin) {
        runTaskTimer(plugin, 0L, 20L);
    }

    // TODO: 03/07/2023 Improve this
    public interface TickAction {
        void execute(int currentSecond);
    }
}