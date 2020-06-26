package com.heroslender.herovender.autosell.strategy;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.listener.autosell.AutoPickupListener;
import com.heroslender.herovender.listener.autosell.AutoPickupOldListener;
import com.heroslender.herovender.listener.autosell.AutoSellListener;
import com.heroslender.herovender.listener.autosell.HeroStackDropsListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Level;

import static org.bukkit.Bukkit.getServer;

public class PickupStrategy implements Strategy {
    private final Listener heroStackDropsListener;
    private final Listener autoPickupListener;
    private final Listener pickupListener;

    public PickupStrategy() {
        final PluginManager pluginManager = getServer().getPluginManager();
        final Plugin heroStackDrops = pluginManager.getPlugin("HeroStackDrops");
        if (heroStackDrops != null && heroStackDrops.isEnabled()) {
            HeroVender.getInstance().getLogger().log(
                    Level.INFO,
                    "HeroStackDrops encontrado(v{0}), ativando compatibilidade.",
                    heroStackDrops.getDescription().getVersion()
            );
            heroStackDropsListener = new HeroStackDropsListener();
        } else {
            heroStackDropsListener = null;
        }

        final Plugin autoPickup = pluginManager.getPlugin("AutoPickup");
        if (autoPickup != null && autoPickup.isEnabled()) {
            HeroVender.getInstance().getLogger().log(
                    Level.INFO,
                    "AutoPickup encontrado(v{0}), ativando compatibilidade.",
                    autoPickup.getDescription().getVersion()
            );

            Listener autoPickupListener;
            try {
                // Test for the AutoPickup version, older versions have different package
                Class.forName("com.philderbeast.autopickup.API.DropToInventoryEvent");

                autoPickupListener = new AutoPickupListener();
            } catch (ClassNotFoundException e) {
                autoPickupListener = new AutoPickupOldListener();
            }

            this.autoPickupListener = autoPickupListener;
        } else {
            autoPickupListener = null;
        }

        pickupListener = new AutoSellListener();
    }

    public void enable() {
        final Plugin plugin = HeroVender.getInstance();
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(pickupListener, plugin);
        if (heroStackDropsListener != null) {
            pluginManager.registerEvents(heroStackDropsListener, plugin);
        }
        if (autoPickupListener != null) {
            pluginManager.registerEvents(autoPickupListener, plugin);
        }
    }

    public void disable() {
        HandlerList.unregisterAll(pickupListener);

        if (heroStackDropsListener != null) {
            HandlerList.unregisterAll(heroStackDropsListener);
        }
        if (autoPickupListener != null) {
            HandlerList.unregisterAll(autoPickupListener);
        }
    }
}
