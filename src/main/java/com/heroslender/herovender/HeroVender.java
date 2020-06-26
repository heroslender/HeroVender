package com.heroslender.herovender;

import com.heroslender.herovender.command.*;
import com.heroslender.herovender.controller.MessageController;
import com.heroslender.herovender.controller.SellBonusController;
import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.controller.UserController;
import com.heroslender.herovender.helpers.CustomFileConfiguration;
import com.heroslender.herovender.helpers.menu.Menu;
import com.heroslender.herovender.listener.autosell.AutoPickupListener;
import com.heroslender.herovender.listener.autosell.AutoPickupOldListener;
import com.heroslender.herovender.listener.autosell.AutoSellListener;
import com.heroslender.herovender.listener.autosell.HeroStackDropsListener;
import com.heroslender.herovender.listener.ShiftSellListener;
import com.heroslender.herovender.listener.UserListener;
import com.heroslender.herovender.service.*;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
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
    @Getter private Economy economy = null;

    public HeroVender() {
        instance = this;

        saveDefaultConfig();

        CustomFileConfiguration messagesConfig = null;
        try {
            messagesConfig = new CustomFileConfiguration("messages", this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "There was an error loading the messages.yml configuration!", e);
        }

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

        services.forEach(Service::init);

        Menu.registar(this);

        new SellCommand(shopController);
        new SellmenuCommand();
        new HerovenderCommand();
        new AutosellCommand();
        new ShiftsellCommand();

        final PluginManager pluginManager = getServer().getPluginManager();
        final Plugin heroStackDrops = pluginManager.getPlugin("HeroStackDrops");
        if (heroStackDrops != null && heroStackDrops.isEnabled()) {
            getLogger().log(
                    Level.INFO,
                    "HeroStackDrops encontrado(v{0}), ativando compatibilidade.",
                    heroStackDrops.getDescription().getVersion()
            );
            registerEvent(new HeroStackDropsListener());
        }

        final Plugin autoPickup = pluginManager.getPlugin("AutoPickup");
        if (autoPickup != null && autoPickup.isEnabled()) {
            getLogger().log(
                    Level.INFO,
                    "AutoPickup encontrado(v{0}), ativando compatibilidade.",
                    autoPickup.getDescription().getVersion()
            );

            Listener autoPickupListener;
            try {
                // Test for the AutoPickup version, older versions have different package
                Class.forName("com.philderbeast.autopickup.API.DropToInventoryEvent");

                autoPickupListener = new AutoPickupListener();
            } catch (ClassNotFoundException e) {
                autoPickupListener = new AutoPickupOldListener();
            }

            registerEvent(autoPickupListener);
        }

        registerEvent(new ShiftSellListener(userController, shopController));
        registerEvent(new AutoSellListener());
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
        services.forEach(Service::init);
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
