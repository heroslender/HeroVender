package com.heroslender.herovender.listener.autosell;

import com.heroslender.herostackdrops.event.PlayerPickupItemEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class HeroStackDropsListener extends AutoSellLogic implements Listener {

    @EventHandler
    private void onPlayerPickupItem(final PlayerPickupItemEvent e) {
        autoSell(e.getPlayer());
    }
}
