package com.heroslender.herovender.listener;

import com.heroslender.herovender.controller.UserController;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class UserListener implements Listener {
    @NonNull private final UserController userController;

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
