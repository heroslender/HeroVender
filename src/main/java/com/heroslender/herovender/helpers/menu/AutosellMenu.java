package com.heroslender.herovender.helpers.menu;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.controller.MessageController;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.helpers.MessageBuilder;
import com.heroslender.herovender.utils.items.MetaItemStack;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AutosellMenu extends Menu {
    private final MessageController messageController;
    private final User user;

    public AutosellMenu(MessageController messageController, final User user) {
        super(messageController.getMessage("sell.menu.title").orElse("Sell Menu"), 27);
        this.messageController = messageController;
        this.user = user;

        val sellItemString = messageController.getMessage("sell.menu.sell").orElse(null);

        val messageBuilder = new MessageBuilder()
                .withPlaceholder(user);

        val metaItemStack = MetaItemStack.getFromString(messageBuilder.build(sellItemString));
        ItemStack itemStack;
        if (metaItemStack == null) {
            itemStack = new HItem(Material.DOUBLE_PLANT, "§aSell", "", "§7Click here to sell your inventory!");
        } else {
            itemStack = metaItemStack.getItemStack();
        }

        setItem(10, itemStack, clickEvent -> {
            HeroVender.getInstance().getShopController().sell(user);
            clickEvent.getWhoClicked().closeInventory();
        });

        setItem(14, getShiftSellMenuItem());
        setItem(16, getAutoSellMenuItem());

        open(user.getPlayer());
    }

    private MenuItem getShiftSellMenuItem() {
        val messageBuilder = new MessageBuilder()
                .withPlaceholder(user);

        if (!user.getPlayer().hasPermission("herovender.shiftsell")){
            val sellItemString = messageController.getMessage("sell.menu.shiftsell.no-permission").orElse(null);

            val metaItemStack = MetaItemStack.getFromString(messageBuilder.build(sellItemString));
            if (metaItemStack == null) {
                return new MenuItem(new HItem(Material.LEVER, "§aShift-Sell",
                        "§7Sell your inventory by sneaking.", "", "§7(Insufficient permissions)"));
            } else {
                return new MenuItem(metaItemStack.getItemStack());
            }
        }

        ItemStack itemStack;
        if (user.isShiftSellActive()) {
            val sellItemString = messageController.getMessage("sell.menu.shiftsell.on").orElse(null);

            val metaItemStack = MetaItemStack.getFromString(messageBuilder.build(sellItemString));
            if (metaItemStack == null) {
                itemStack = new HItem(Material.LEVER, "§aShift-Sell",
                        "§7Sell your inventory by sneaking.", "", "§7Current state: §aActive", "§7(Click to deactivate)");
            } else {
                itemStack = metaItemStack.getItemStack();
            }
        } else {
            val sellItemString = messageController.getMessage("sell.menu.shiftsell.off").orElse(null);

            val metaItemStack = MetaItemStack.getFromString(messageBuilder.build(sellItemString));
            if (metaItemStack == null) {
                itemStack = new HItem(Material.LEVER, "§cShift-Sell",
                        "§7Sell your inventory by sneaking.", "", "§7Current state: §cInactive", "§7(Click to activate)");
            } else {
                itemStack = metaItemStack.getItemStack();
            }
        }

        return new MenuItem(itemStack, itemClick -> {
            user.toggleShiftSell();
            setItemUpdate(14, itemClick.getWhoClicked(), getShiftSellMenuItem());
        });
    }

    private MenuItem getAutoSellMenuItem() {
        val messageBuilder = new MessageBuilder()
                .withPlaceholder(user);

        if (!user.getPlayer().hasPermission("herovender.autosell")){
            val sellItemString = messageController.getMessage("sell.menu.autosell.no-permission").orElse(null);

            val metaItemStack = MetaItemStack.getFromString(messageBuilder.build(sellItemString));
            if (metaItemStack == null) {
                return new MenuItem(new HItem(Material.LEVER, "§aAuto-Sell",
                        "§7Automatically sell your inventory when full.", "", "§7(Insufficient permissions)"));
            } else {
                return new MenuItem(metaItemStack.getItemStack());
            }
        }

        ItemStack itemStack;
        if (user.isAutoSellActive()) {
            val sellItemString = messageController.getMessage("sell.menu.autosell.on").orElse(null);

            val metaItemStack = MetaItemStack.getFromString(messageBuilder.build(sellItemString));
            if (metaItemStack == null) {
                itemStack = new HItem(Material.LEVER, "§aAuto-Sell",
                        "§7Automatically sell your inventory when full.", "", "§7Current state: §aActive", "§7(Click to deactivate)");
            } else {
                itemStack = metaItemStack.getItemStack();
            }
        } else {
            val sellItemString = messageController.getMessage("sell.menu.autosell.off").orElse(null);

            val metaItemStack = MetaItemStack.getFromString(messageBuilder.build(sellItemString));
            if (metaItemStack == null) {
                itemStack = new HItem(Material.LEVER, "§cAuto-Sell",
                        "§7Automatically sell your inventory when full.", "", "§7Current state: §cInactive", "§7(Click to activate)");
            } else {
                itemStack = metaItemStack.getItemStack();
            }
        }

        return new MenuItem(itemStack, itemClick -> {
            user.toggleAutoSell();
            setItemUpdate(16, itemClick.getWhoClicked(), getAutoSellMenuItem());
        });
    }
}
