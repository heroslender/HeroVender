package com.github.heroslender.herovender.autosell.strategy;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.autosell.AutoSellLogic;
import com.github.heroslender.herovender.controller.ShopController;
import com.github.heroslender.herovender.controller.UserController;
import com.github.heroslender.herovender.service.AutoSellService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PickupStrategy extends AutoSellLogic implements Strategy, Listener {

    public PickupStrategy(UserController userController, ShopController shopController, AutoSellService autoSellService) {
        super(userController, shopController, autoSellService);
    }

    public void enable() {
        final Plugin plugin = HeroVender.getInstance();
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(this, plugin);
    }

    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerPickupItem(final EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player player) {
            autoSell(player);
        }
    }
}
