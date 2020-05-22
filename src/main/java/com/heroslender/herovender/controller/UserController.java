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
        int delay = -1;

        for (Map.Entry<String, Integer> entry : userService.getDelays().entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                int entryValue = entry.getValue();
                if (delay == -1 || delay > entryValue) {
                    delay = entryValue;
                }
            }
        }

        return delay;
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void remove(final String id) {
        userService.remove(id);
    }
}
