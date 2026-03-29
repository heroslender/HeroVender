package com.github.heroslender.herovender.autosell.strategy;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.controller.ShopController;
import com.github.heroslender.herovender.controller.UserController;
import com.github.heroslender.herovender.autosell.AutoSellLogic;
import com.github.heroslender.herovender.service.AutoSellService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import static org.bukkit.Bukkit.getServer;

public class TimerStrategy extends AutoSellLogic implements Strategy, Runnable {
    private final long getAutoSellTimerDelay;
    private BukkitTask task;

    public TimerStrategy(UserController userController, ShopController shopController, AutoSellService autoSellService, long autoSellTimerDelay) {
        super(userController, shopController, autoSellService);
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
