package com.heroslender.herovender.listener;

import com.heroslender.herovender.controller.UserController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserListener implements Listener {
    private final UserController userController;

    public UserListener(UserController userController) {
        this.userController = userController;
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        userController.remove(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerKick(PlayerKickEvent e) {
        if (!e.isCancelled()) {
            userController.remove(e.getPlayer().getName());
        }
    }
}
