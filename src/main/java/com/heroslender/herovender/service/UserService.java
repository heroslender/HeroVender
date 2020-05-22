package com.heroslender.herovender.service;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.data.User;
import lombok.Getter;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class UserService implements Service<User> {
    private final Map<String, User> users = new HashMap<>();
    @Getter private final Map<String, Integer> delays = new HashMap<>();

    @Override
    public void init() {
        final ConfigurationSection config = HeroVender.getInstance().getConfig().getConfigurationSection("delay");
        if (config != null) {
            for (String id : config.getKeys(false)) {
                String permission = config.getString(id + ".permission");
                int delay = config.getInt(id + ".delay", -1);
                if (permission != null && delay >= 0) {
                    delays.put(permission, delay);
                }
            }
        }
    }

    @Override
    public List<User> get() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(String id) {
        return Optional.ofNullable(get(id));
    }

    @Nullable
    public User get(@NotNull String id) {
        return users.get(id);
    }

    @NotNull
    public User getOrCreate(@NotNull final Player player) {
        User user = get(player.getName());
        if (user == null) {
            user = new User(player);
            users.put(player.getName(), user);
        }

        return user;
    }

    public void remove(final String id) {
        users.remove(id);
    }

    @Override
    public void stop() {
        users.clear();
        delays.clear();
    }
}
