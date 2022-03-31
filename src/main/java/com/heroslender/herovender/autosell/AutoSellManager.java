package com.heroslender.herovender.autosell;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.autosell.strategy.PickupStrategy;
import com.heroslender.herovender.autosell.strategy.Strategy;
import com.heroslender.herovender.autosell.strategy.TimerStrategy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

@RequiredArgsConstructor
public class AutoSellManager {
    private final Plugin plugin;
    private Strategy strategy;

    @Getter private AutoSellStategy autoSellStrategy;
    @Getter private boolean autoSellInvFull;
    @Getter private long autoSellTimerDelay = 0;

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

        if (getAutoSellStrategy() == AutoSellStategy.PICKUP_ITEM) {
            strategy = new PickupStrategy();
        } else if (getAutoSellStrategy() == AutoSellStategy.TIMER) {
            if (!getConfig().isSet("autosell.settings.timer-delay")) {
                getConfig().set("autosell.settings.timer-delay", 10);
                saveConfig();
            }

            autoSellTimerDelay = getConfig().getLong("autosell.settings.timer-delay", 10);
            strategy = new TimerStrategy(getAutoSellTimerDelay());
        }

        plugin.getLogger().info("Autosell carregado:");
        plugin.getLogger().info(" -> Metodo: " + getAutoSellStrategy().name());
        plugin.getLogger().info(" -> Full Inv: " + autoSellInvFull);
        if (getAutoSellStrategy() == AutoSellStategy.TIMER) {
            plugin.getLogger().info(" -> Timer Delay: " + autoSellTimerDelay);
        }

        strategy.enable();
    }

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
