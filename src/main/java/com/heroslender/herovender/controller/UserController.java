package com.heroslender.herovender.controller;

import com.heroslender.herovender.data.User;
import com.heroslender.herovender.service.UserService;
import org.bukkit.entity.Player;

import java.util.Map;

public class UserController {
    private final UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public User getOrCreate(Player player) {
        return userService.getOrCreate(player);
    }

    public int getDelay(Player player) {
        return userService.getDelays().entrySet().stream()
                .filter(entry -> player.hasPermission(entry.getKey()))
                .mapToInt(Map.Entry::getValue)
                .min().orElse(0);
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public boolean isAutosellActive(final Player player) {
        return isAutosellActive(player.getName());
    }

    public boolean isAutosellActive(final String player) {
        return userService.getById(player).map(User::isShiftSellActive).orElse(false);
    }

    public void remove(final String id) {
        userService.remove(id);
    }
}
