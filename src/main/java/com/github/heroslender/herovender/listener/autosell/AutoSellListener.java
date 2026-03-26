package com.github.heroslender.herovender.listener.autosell;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

@RequiredArgsConstructor
public class AutoSellListener extends AutoSellLogic implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onPlayerPickupItem(final EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player player) {
            autoSell(player);
        }
    }
}
