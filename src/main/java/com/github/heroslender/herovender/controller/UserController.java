package com.github.heroslender.herovender.controller;

import com.github.heroslender.herovender.data.User;
import com.github.heroslender.herovender.service.UserService;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Map;

@AllArgsConstructor
public class UserController {
    private final UserService userService;

    public synchronized User getOrCreate(Player player) {
        return userService.getOrCreate(player);
    }

    public synchronized int getDelay(Player player) {
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

    public synchronized void remove(final String id) {
        userService.remove(id);
    }
}
