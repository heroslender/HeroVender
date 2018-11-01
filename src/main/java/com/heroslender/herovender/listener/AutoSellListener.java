package com.heroslender.herovender.listener;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.SellDelayException;
import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.controller.UserController;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.utils.HeroException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

@RequiredArgsConstructor
public class AutoSellListener implements Listener {
    private final UserController userController;
    private final ShopController shopController;

    @EventHandler
    private void onPlayerPickupItem(final EntityPickupItemEvent e) {
        if (!e.isCancelled() && e.getEntity() instanceof Player) {
            val user = userController.getOrCreate((Player) e.getEntity());

            if (user.isAutoSellActive() && user.getEmptySlotsCount() <= 1) {
                autoSell(user);
            }
        }
    }

    private void autoSell(final User user){
        try {
            shopController.sell(user);
        } catch (SellDelayException ex) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroVender.getInstance(), () -> autoSell(user), ex.getDelay() / 50 + 1);
        } catch (HeroException ex) {
            user.sendMessage(ex.getMessage());
        }
    }
}
