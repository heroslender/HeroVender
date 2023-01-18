package com.heroslender.herovender.menu.impl;

import com.heroslender.herostackdrops.nms.ItemStackDeserializer;
import com.heroslender.herovender.Config;
import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.controller.MessageController;
import com.heroslender.herovender.data.SellReason;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.helpers.MessageBuilder;
import com.heroslender.herovender.menu.Menu;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AutosellMenu extends Menu {
    private final ItemStackDeserializer itemStackDeserializer;
    private final MessageController messageController;
    private final User user;

    public AutosellMenu(MessageController messageController, final User user) {
        super(messageController.getMessage("sell.menu.title").orElse("Sell Menu"), 27);
        this.messageController = messageController;
        this.user = user;
        this.itemStackDeserializer = HeroVender.getInstance().getNmsManager().getDeserializer();

        val messageBuilder = new MessageBuilder()
                .withPlaceholder(user);

        val sellItem = getItemStack("sell.menu.sell", messageBuilder);
        if (sellItem != null) {
            setItem(10, sellItem, clickEvent -> {
                HeroVender.getInstance().getShopController().sell(user, SellReason.COMMAND);
                clickEvent.getWhoClicked().closeInventory();
            });
        }

        val pricesItem = getItemStack("sell.menu.sell-prices", messageBuilder);
        if (pricesItem != null) {
            setItem(12, pricesItem, clickEvent -> {
                Bukkit.getScheduler().runTaskAsynchronously(HeroVender.getInstance(), () ->
                        new ShopPricesMenu(messageController, user)
                );
            });
        }

        setItem(14, getShiftSellMenuItem());
        setItem(16, getAutoSellMenuItem());

        open(user.getPlayer());
    }

    @Nullable
    private ItemStack getItemStack(String messageId, MessageBuilder builder) {
        if (itemStackDeserializer == null) {
            return null;
        }

        val sellItemString = messageController.getMessage(messageId).orElse(null);
        return itemStackDeserializer.deserialize(builder.build(sellItemString));
    }

    @Nullable
    private MenuItem getShiftSellMenuItem() {
        val messageBuilder = new MessageBuilder()
                .withPlaceholder(user);

        if (!user.getPlayer().hasPermission(Config.SHIFTSELL_PERMISSION)) {
            ItemStack itemStack = getItemStack("sell.menu.shiftsell.no-permission", messageBuilder);
            if (itemStack == null) {
                return null;
            }

            return new MenuItem(itemStack);
        }

        ItemStack itemStack;
        if (user.isShiftSellActive()) {
            itemStack = getItemStack("sell.menu.shiftsell.on", messageBuilder);
        } else {
            itemStack = getItemStack("sell.menu.shiftsell.off", messageBuilder);
        }

        if (itemStack == null) {
            return null;
        }

        return new MenuItem(itemStack, itemClick -> {
            user.toggleShiftSell();
            setItemUpdate(14, itemClick.getWhoClicked(), getShiftSellMenuItem());
        });
    }

    private MenuItem getAutoSellMenuItem() {
        val messageBuilder = new MessageBuilder()
                .withPlaceholder(user);

        if (!user.getPlayer().hasPermission(Config.AUTOSELL_PERMISSION)) {
            ItemStack itemStack = getItemStack("sell.menu.autosell.no-permission", messageBuilder);
            if (itemStack == null) {
                return null;
            }

            return new MenuItem(itemStack);
        }

        ItemStack itemStack;
        if (user.isAutoSellActive()) {
            itemStack = getItemStack("sell.menu.autosell.on", messageBuilder);
        } else {
            itemStack = getItemStack("sell.menu.autosell.off", messageBuilder);
        }

        if (itemStack == null) {
            return null;
        }

        return new MenuItem(itemStack, itemClick -> {
            user.toggleAutoSell();
            setItemUpdate(16, itemClick.getWhoClicked(), getAutoSellMenuItem());
        });
    }

}
