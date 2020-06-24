package com.heroslender.herovender.listener.autosell;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

@RequiredArgsConstructor
public class AutoSellListener extends AutoSellLogic implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onPlayerPickupItem(final PlayerPickupItemEvent e) {
        autoSell(e.getPlayer());
    }
}
