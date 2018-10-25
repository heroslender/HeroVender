package com.heroslender.herovender.helpers;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.controller.MessageController;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.utils.items.MetaItemStack;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AutosellMenu extends Menu {
    private final MessageController messageController;
    private final User user;

    public AutosellMenu(MessageController messageController, final User user) {
        super(messageController.getMessage("autosell.menu.title").orElse("Sell Menu"), 27);
        this.messageController = messageController;
        this.user = user;

        val sellItemString = messageController.getMessage("autosell.menu.sell").orElse(null);

        val messageBuilder = new MessageBuilder()
                .withPlaceholder(user);

        val metaItemStack = MetaItemStack.getFromString(messageBuilder.build(sellItemString));
        ItemStack itemStack;
        if (metaItemStack == null) {
            itemStack = new HItem(Material.DOUBLE_PLANT, "§aSell", "", "§7Click here to sell your inventory!");
        } else {
            itemStack = metaItemStack.getItemStack();
        }

        setItem(11, itemStack, clickEvent -> {
            HeroVender.getInstance().getShopController().sell(user);
            clickEvent.getWhoClicked().closeInventory();
        });

        setItem(15, getAutosellMenuItem());

        open(user.getPlayer());
    }

    private MenuItem getAutosellMenuItem() {
        val messageBuilder = new MessageBuilder()
                .withPlaceholder(user);

        ItemStack itemStack;
        if (user.isShiftSellActive()) {
            val sellItemString = messageController.getMessage("autosell.menu.autosell.on").orElse(null);

            val metaItemStack = MetaItemStack.getFromString(messageBuilder.build(sellItemString));
            if (metaItemStack == null) {
                itemStack = new HItem(Material.LEVER, "§aAuto-Sell",
                        "§7Sell your inventory by sneaking.", "", "§7Current state: §aActive", "§7(Click to deactivate)");
            } else {
                itemStack = metaItemStack.getItemStack();
            }
        } else {
            val sellItemString = messageController.getMessage("autosell.menu.autosell.off").orElse(null);

            val metaItemStack = MetaItemStack.getFromString(messageBuilder.build(sellItemString));
            if (metaItemStack == null) {
                itemStack = new HItem(Material.LEVER, "§cAuto-Sell",
                        "§7Sell your inventory by sneaking.", "", "§7Current state: §cInactive", "§7(Click to activate)");
            } else {
                itemStack = metaItemStack.getItemStack();
            }
        }

        return new MenuItem(itemStack, itemClick -> {
            user.toggleShiftSell();
            setItemUpdate(15, itemClick.getWhoClicked(), getAutosellMenuItem());
        });
    }
}
