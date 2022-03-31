package com.heroslender.herovender.listener;

import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.controller.UserController;
import com.heroslender.herovender.utils.HeroException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

@RequiredArgsConstructor
public class ShiftSellListener implements Listener {
    private final UserController userController;
    private final ShopController shopController;

    private final boolean chat;
    private final boolean actionbar;
    private final boolean ignoreEmpty;

    public ShiftSellListener(UserController userController, ShopController shopController, Configuration messagesConfig) {
        this.userController = userController;
        this.shopController = shopController;

        this.chat = messagesConfig.getBoolean("sell.command.chat", true);
        this.actionbar = messagesConfig.getBoolean("sell.command.actionbar", true);
        this.ignoreEmpty = messagesConfig.getBoolean("sell.command.ignore-empty", true);
    }

    @EventHandler
    private void onPlayerSneak(final PlayerToggleSneakEvent e) {
        // When the player releases the sneak button only
        if (!e.isCancelled() && !e.isSneaking()) {
            val user = userController.getOrCreate(e.getPlayer());

            if (user.isShiftSellActive()) {
                try {
                    shopController.sell(user, chat, actionbar, ignoreEmpty);
                } catch (HeroException ex) {
                    user.sendMessage(ex.getMessage());
                }
            }
        }
    }
}
