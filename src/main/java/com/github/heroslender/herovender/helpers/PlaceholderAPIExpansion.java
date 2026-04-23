package com.github.heroslender.herovender.helpers;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.data.User;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    private final HeroVender plugin;

    public PlaceholderAPIExpansion(HeroVender plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "herovender";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        User user = plugin.getUserController().getOrCreate(player);

        if (params.equals("autosell_active")) {
            return String.valueOf(user.isAutoSellActive());
        }

        if (params.equals("shiftsell_active")) {
            return String.valueOf(user.isShiftSellActive());
        }

        return null;
    }
}
