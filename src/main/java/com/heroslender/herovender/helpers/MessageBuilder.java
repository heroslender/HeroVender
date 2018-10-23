package com.heroslender.herovender.helpers;

import com.heroslender.herovender.data.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessageBuilder {
    private static final Map<String, String> defaultPlaceholder = Collections.emptyMap();
    private final Map<String, String> placeholders;

    public MessageBuilder() {
        placeholders = new HashMap<>(defaultPlaceholder);
    }

    public MessageBuilder withPlaceholder(User user) {
        return withPlaceholder(user.getPlayer());
    }

    public MessageBuilder withPlaceholder(Player player) {
        return withPlaceholder((CommandSender) player);
    }

    public MessageBuilder withPlaceholder(CommandSender sender) {
        withPlaceholder("player", sender.getName());
        return this;
    }

    public MessageBuilder withPlaceholder(String replacer, double placeholder) {
        return withPlaceholder(replacer, Double.toString(placeholder));
    }

    public MessageBuilder withPlaceholder(String replacer, int placeholder) {
        return withPlaceholder(replacer, Integer.toString(placeholder));
    }

    public MessageBuilder withPlaceholder(String replacer, String placeholder) {
        if (replacer == null || replacer.isEmpty() || placeholder == null)
            return this;

        if (replacer.startsWith(":") && replacer.endsWith(":"))
            placeholders.put(replacer, placeholder);
        else
            placeholders.put(":" + replacer + ":", placeholder);

        return this;
    }

    public String build(String message) {
        if (message == null) {
            return null;
        }

        for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            message = message.replace(placeholder.getKey(), placeholder.getValue());
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
