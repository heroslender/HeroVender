package com.heroslender.herovender.listener.autosell;

import me.MnMaxon.AutoPickup.API.DropToInventoryEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AutoPickupOldListener extends AutoSellLogic implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onPickup(final DropToInventoryEvent e) {
        autoSell(e.getPlayer());
    }
}
