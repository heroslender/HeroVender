package com.github.heroslender.herovender.listener;

import com.github.heroslender.herovender.controller.UserController;
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
    private void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        userController.getOrCreate(e.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        userController.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerKick(PlayerKickEvent e) {
        userController.remove(e.getPlayer().getUniqueId());
    }
}
