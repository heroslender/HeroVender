package com.heroslender.herovender.controller;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.SellDelayException;
import com.heroslender.herovender.data.*;
import com.heroslender.herovender.event.PlayerSellEvent;
import com.heroslender.herovender.helpers.MessageBuilder;
import com.heroslender.herovender.service.ShopService;
import com.heroslender.herovender.utils.HeroException;
import com.heroslender.herovender.utils.NmsUtils;
import lombok.NonNull;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    public Shop[] getShopsFor(User user) {
        return shopService.get().stream()
                .filter(shop -> shop.getPermission() == null || user.getPlayer().hasPermission(shop.getPermission()))
                .sorted(Comparator.comparingInt(Shop::getPriority))
                .toArray(Shop[]::new);
    }

    public Invoice sell(@NonNull final User user) throws HeroException {
        return sell(user, true, false);
    }

    /**
     * Sell the player's inventory to the shops he has access to
     *
     * @param user      The player selling the inventory
     * @param chat      Send the message in the player chat?
     * @param actionBar Send the message in the player action bar?
     * @return {@link Invoice} from the sell, or null if the {@link PlayerSellEvent} was cancelled
     */
    public Invoice sell(@NonNull final User user, final boolean chat, final boolean actionBar) throws HeroException {
        val invoice = sellSilent(user);
        if (invoice == null) {
            // PlayerSellEvent was cancelled
            return null;
        }

        if (chat || actionBar) {
            val message = invoice.getItems().isEmpty()
                    ? HeroVender.getInstance().getMessageController().getMessage("sell.no-items")
                    : HeroVender.getInstance().getMessageController().getMessage("sell.sold");

            message.ifPresent(msg -> {
                MessageBuilder messageBuilder = new MessageBuilder()
                        .withPlaceholder(user)
                        .withPlaceholder(invoice);

                val toSend = messageBuilder.build(msg);

                if (chat) {
                    user.sendMessage(toSend);
                }
                if (actionBar) {
                    NmsUtils.sendActionBar(toSend, user.getPlayer());
                }
            });
        }

        return invoice;
    }

    /**
     * Sell the player's inventory to the shops he has access to, withou sending him a message
     *
     * @param user The player selling the inventory
     * @return {@link Invoice} from the sell, or null if the {@link PlayerSellEvent} was cancelled
     */
    public Invoice sellSilent(@NonNull final User user) throws HeroException {
        return sellSilent(user, getShopsFor(user));
    }

    /**
     * Sell the player's inventory, to the shops defined, withou sending him a message
     *
     * @param user  The player selling the inventory
     * @param shops The shops to sell to
     * @return {@link Invoice} from the sell, or null if the {@link PlayerSellEvent} was cancelled
     */
    public Invoice sellSilent(@NonNull final User user, @NonNull final Shop... shops) throws SellDelayException {
        user.checkDelay();

        val inventory = user.getInventory();
        val toSell = new ArrayList<SellItem>();

        ShopItem cached = null;
        for (int i = 0; i < inventory.getContents().length; i++) {
            val itemStack = inventory.getItem(i);
            if (cached == null || !cached.getItemStack().isSimilar(itemStack)) {
                // Item isn't the same as the prev sold, so fetch it's price
                val shopItemOpt = getShopItem(itemStack, shops);
                if (!shopItemOpt.isPresent()) {
                    continue;
                }

                cached = shopItemOpt.get();
            }
            inventory.setItem(i, null);

            int amount = itemStack.getAmount();

            // The item can be sold! Update them on the `to-sell` list
            for (SellItem sellingItem : toSell) {
                if (sellingItem.getItemStack().isSimilar(itemStack)) {
                    amount += sellingItem.getAmount();
                    toSell.remove(sellingItem);
                    break;
                }
            }
            toSell.add(new SellItem(itemStack, cached.getPrice(), amount));
        }

        val preInvoice = new Invoice(toSell);

        user.getSellBonus().ifPresent(bonus -> preInvoice.getBonuses().add(bonus));

        val event = new PlayerSellEvent(user.getPlayer(), preInvoice);
        Bukkit.getServer().getPluginManager().callEvent(event);

        val invoice = event.getInvoice();
        HeroVender.getInstance().getEconomy().depositPlayer(user.getPlayer(), invoice.getTotal());

        return invoice;
    }

    private Optional<ShopItem> getShopItem(ItemStack itemStack, Shop... shops) {
        val available = new ArrayList<ShopItem>();
        for (Shop shop : shops) {
            shop.getShopItem(itemStack)
                    .ifPresent(available::add);
        }

        if (available.isEmpty()) {
            return Optional.empty();
        } else if (available.size() == 1) {
            return Optional.of(available.get(0));
        }

        ShopItem highest = available.get(0);
        for (int i = 1; i < available.size(); i++) {
            val item = available.get(i);
            if (item.getPrice() > highest.getPrice()) {
                highest = item;
            }
        }

        return Optional.of(highest);
    }
}
