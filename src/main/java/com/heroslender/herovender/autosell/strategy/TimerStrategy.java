package com.heroslender.herovender.autosell.strategy;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.listener.autosell.AutoSellLogic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import static org.bukkit.Bukkit.getServer;

public class TimerStrategy extends AutoSellLogic implements Strategy, Runnable {
    private final long getAutoSellTimerDelay;
    private BukkitTask task;

    public TimerStrategy(long autoSellTimerDelay) {
        this.getAutoSellTimerDelay = autoSellTimerDelay;
    }

    @Override
    public void enable() {
        task = getServer()
                .getScheduler()
                .runTaskTimerAsynchronously(HeroVender.getInstance(), this, 1, getAutoSellTimerDelay);
    }

    @Override
    public void disable() {
        task.cancel();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            autoSell(player);
        }
    }
}
