package com.heroslender.herovender.listener;

import com.heroslender.herostackdrops.event.PlayerPickupItemEvent;
import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.SellDelayException;
import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.controller.UserController;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.utils.HeroException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class HeroStackDropsListener implements Listener {
    private final UserController userController;
    private final ShopController shopController;

    @EventHandler
    private void onPlayerPickupItem(final PlayerPickupItemEvent e) {
        val user = userController.getOrCreate(e.getPlayer());

        if (user.isAutoSellActive() && user.getEmptySlotsCount() <= 1) {
            autoSell(user);
        }
    }

    private void autoSell(final User user){
        try {
            shopController.sell(user, false, true);
        } catch (SellDelayException ex) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroVender.getInstance(), () -> autoSell(user), ex.getDelay() / 50 + 1);
        } catch (HeroException ex) {
            user.sendMessage(ex.getMessage());
        }
    }
}
