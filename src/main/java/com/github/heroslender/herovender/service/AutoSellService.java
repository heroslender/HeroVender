package com.github.heroslender.herovender.service;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.autosell.AutoSellStategy;
import com.github.heroslender.herovender.autosell.strategy.PickupStrategy;
import com.github.heroslender.herovender.autosell.strategy.Strategy;
import com.github.heroslender.herovender.autosell.strategy.TimerStrategy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;
import java.util.logging.Level;

@RequiredArgsConstructor
public class AutoSellService implements Service {
    private final HeroVender plugin;
    private Strategy strategy;

    @Getter
    private AutoSellStategy autoSellStrategy;
    @Getter
    private boolean autoSellInvFull;
    @Getter
    private long autoSellTimerDelay = 0;

    @Override
    public void init() {
        // Load auto-sell related config
        if (!getConfig().isSet("autosell.strategy")) {
            getConfig().set("autosell.strategy", "PICKUP_ITEM");
            saveConfig();
        }
        if (!getConfig().isSet("autosell.settings.require-inventory-full")) {
            getConfig().set("autosell.settings.require-inventory-full", true);
            saveConfig();
        }

        this.autoSellStrategy = AutoSellStategy.valueOf(
                getConfig().getString("autosell.strategy", "PICKUP_ITEM").toUpperCase(Locale.ROOT)
        );
        this.autoSellInvFull = getConfig().getBoolean("autosell.settings.require-inventory-full", true);

        val userController = plugin.getUserController();
        val shopController = plugin.getShopController();
        if (getAutoSellStrategy() == AutoSellStategy.PICKUP_ITEM) {
            strategy = new PickupStrategy(userController, shopController, this);
        } else if (getAutoSellStrategy() == AutoSellStategy.TIMER) {
            if (!getConfig().isSet("autosell.settings.timer-delay")) {
                getConfig().set("autosell.settings.timer-delay", 10);
                saveConfig();
            }

            autoSellTimerDelay = getConfig().getLong("autosell.settings.timer-delay", 10);
            strategy = new TimerStrategy(userController, shopController, this, getAutoSellTimerDelay());
        }

        plugin.getLogger().log(Level.INFO, "Autosell loaded:");
        plugin.getLogger().log(Level.INFO, " -> Method: {0}", getAutoSellStrategy().name());
        plugin.getLogger().log(Level.INFO, " -> Full Inv: {0}", autoSellInvFull);
        if (getAutoSellStrategy() == AutoSellStategy.TIMER) {
            plugin.getLogger().log(Level.INFO, " -> Timer Delay: {0}", autoSellTimerDelay);
        }

        strategy.enable();
    }

    @Override
    public void stop() {
        strategy.disable();
    }

    private void saveConfig() {
        plugin.saveConfig();
    }

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
}
