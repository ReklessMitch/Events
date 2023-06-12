package me.reklessmitch.csgo.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {
    private int currentSecond;
    private TickAction tickAction;
    private Runnable completeAction;

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

    public void start(JavaPlugin plugin) {
        runTaskTimer(plugin, 0L, 20L);
    }

    public interface TickAction {
        void execute(int currentSecond);
    }
}