package com.github.heroslender.herovender.autosell.strategy;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.listener.autosell.AutoSellListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PickupStrategy implements Strategy {
    private final Listener pickupListener;

    public PickupStrategy() {
        pickupListener = new AutoSellListener();
    }

    public void enable() {
        final Plugin plugin = HeroVender.getInstance();
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(pickupListener, plugin);
    }

    public void disable() {
        HandlerList.unregisterAll(pickupListener);
    }
}
