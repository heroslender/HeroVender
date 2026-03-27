package com.github.heroslender.herovender;

import com.github.heroslender.herovender.autosell.AutoSellManager;
import com.github.heroslender.herovender.command.AutosellCommand;
import com.github.heroslender.herovender.command.HerovenderCommand;
import com.github.heroslender.herovender.command.SellCommand;
import com.github.heroslender.herovender.command.ShiftsellCommand;
import com.github.heroslender.herovender.controller.SellBonusController;
import com.github.heroslender.herovender.controller.ShopController;
import com.github.heroslender.herovender.controller.UserController;
import com.github.heroslender.herovender.helpers.CustomFileConfiguration;
import com.github.heroslender.herovender.listener.ShiftSellListener;
import com.github.heroslender.herovender.listener.UserListener;
import com.github.heroslender.herovender.service.*;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class HeroVender extends JavaPlugin {
    @Getter
    private static HeroVender instance;

    private final List<Service> services = new ArrayList<>();

    @Getter
    private final ShopController shopController;
    @Getter
    private final UserController userController;
    @Getter
    private final SellBonusController sellBonusController;

    @Getter
    private final AutoSellManager autoSellManager;

    @Getter
    private CustomFileConfiguration messagesConfig = null;

    @Getter
    private Economy economy = null;

    public HeroVender() {
        instance = this;

        saveDefaultConfig();
        try {
            messagesConfig = new CustomFileConfiguration("messages", this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "There was an error loading the messages.yml configuration!", e);
        }

        this.autoSellManager = new AutoSellManager(this);

        MessageService messageService = new MessageService(messagesConfig);
        services.add(messageService);

        ShopService shopService = new ShopService();
        shopController = new ShopController(shopService);
        services.add(shopService);

        UserService userService = new UserService();
        userController = new UserController(userService);
        services.add(userService);

        SellBonusService sellBonusService = new SellBonusService();
        sellBonusController = new SellBonusController(sellBonusService);
        services.add(sellBonusService);
    }

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        autoSellManager.init();

        services.forEach(Service::init);

        new SellCommand(shopController);
        new HerovenderCommand();
        new AutosellCommand();
        new ShiftsellCommand();

        registerEvent(new ShiftSellListener(userController, shopController));
        registerEvent(new UserListener(userController));

        new Metrics(this, 3757);
    }

    private void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        services.forEach(Service::stop);
    }

    public void reload() {
        reloadConfig();
        services.forEach(Service::stop);
        autoSellManager.stop();
        services.forEach(Service::init);
        autoSellManager.init();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
}
