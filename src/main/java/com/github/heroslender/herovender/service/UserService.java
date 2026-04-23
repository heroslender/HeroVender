package com.github.heroslender.herovender.service;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.data.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@RequiredArgsConstructor
public class UserService implements Service {
    private final HeroVender plugin;
    private final Map<UUID, User> users = new HashMap<>();
    @Getter private final Map<String, Integer> delays = new HashMap<>();

    @Override
    public void init() {
        final ConfigurationSection config = plugin.getConfig().getConfigurationSection("delay");
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

    public List<User> get() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> getById(UUID id) {
        return Optional.ofNullable(get(id));
    }

    @Nullable
    public User get(@NotNull UUID id) {
        return users.get(id);
    }

    @NotNull
    public User getOrCreate(@NotNull final Player player) {
        User user = get(player.getUniqueId());
        if (user == null) {
            user = new User(player);
            users.put(player.getUniqueId(), user);
            
            // Load from storage
            plugin.getStorageService().loadUser(user);
        }

        return user;
    }

    public void remove(final UUID id) {
        users.remove(id);
    }

    public void save(User user) {
        plugin.getStorageService().saveUser(user);
    }

    @Override
    public void stop() {
        users.clear();
        delays.clear();
    }
}
