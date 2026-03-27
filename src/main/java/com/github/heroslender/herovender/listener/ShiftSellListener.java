package com.github.heroslender.herovender.listener;

import com.github.heroslender.herovender.command.exception.SellDelayException;
import com.github.heroslender.herovender.controller.ShopController;
import com.github.heroslender.herovender.controller.UserController;
import com.github.heroslender.herovender.data.SellReason;
import com.github.heroslender.herovender.utils.HeroException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

@RequiredArgsConstructor
public class ShiftSellListener implements Listener {
    private final UserController userController;
    private final ShopController shopController;

    @EventHandler
    private void onPlayerSneak(final PlayerToggleSneakEvent e) {
        // When the player releases the sneak button only
        if (!e.isCancelled() && !e.isSneaking()) {
            val user = userController.getOrCreate(e.getPlayer());

            if (user.isShiftSellActive()) {
                try {
                    shopController.sell(user, SellReason.SHIFT);
                } catch (SellDelayException ex) {
                    ex.sendUserMessage();
                } catch (HeroException ex) {
                    user.sendMessage(ex.getMessage());
                }
            }
        }
    }
}
