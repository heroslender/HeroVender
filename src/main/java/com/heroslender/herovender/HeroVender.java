package com.heroslender.herovender;

import com.heroslender.herovender.autosell.AutoSellManager;
import com.heroslender.herovender.command.*;
import com.heroslender.herovender.controller.MessageController;
import com.heroslender.herovender.controller.SellBonusController;
import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.controller.UserController;
import com.heroslender.herovender.helpers.CustomFileConfiguration;
import com.heroslender.herovender.helpers.menu.Menu;
import com.heroslender.herovender.listener.ShiftSellListener;
import com.heroslender.herovender.listener.UserListener;
import com.heroslender.herovender.service.*;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class HeroVender extends JavaPlugin {
    @Getter private static HeroVender instance;

    private final List<Service> services = new ArrayList<>();

    @Getter private final ShopController shopController;
    @Getter private final MessageController messageController;
    @Getter private final UserController userController;
    @Getter private final SellBonusController sellBonusController;

    @Getter private final AutoSellManager autoSellManager;

    @Getter private CustomFileConfiguration messagesConfig = null;

    @Getter private Economy economy = null;

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
        messageController = new MessageController(messageService);
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

        Menu.registar(this);

        new SellCommand(shopController);
        new SellmenuCommand();
        new HerovenderCommand();
        new AutosellCommand();
        new ShiftsellCommand();

        registerEvent(new ShiftSellListener(userController, shopController));
        registerEvent(new UserListener(userController));

        new Metrics(this);
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
        return economy != null;
    }
}
