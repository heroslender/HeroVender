package com.heroslender.herovender.listener;

import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.controller.UserController;
import com.heroslender.herovender.utils.HeroException;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class AutosellListener implements Listener {
    private final UserController userController;
    private final ShopController shopController;

    public AutosellListener(UserController userController, ShopController shopController) {
        this.userController = userController;
        this.shopController = shopController;
    }

    @EventHandler
    private void onPlayerSneak(final PlayerToggleSneakEvent e) {
        // When the player releases the sneak button only
        if (!e.isCancelled() && !e.isSneaking()) {
            val player = e.getPlayer();
            val user = userController.getOrCreate(player);

            if (user.isShiftSellActive()) {
                try {
                    shopController.sell(user);
                } catch (HeroException ex) {
                    player.sendMessage(ex.getMessage());
                }
            }
        }
    }
}
