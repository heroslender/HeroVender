package com.heroslender.herovender.controller;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.data.Invoice;
import com.heroslender.herovender.data.SellItem;
import com.heroslender.herovender.data.Shop;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.event.PlayerSellEvent;
import com.heroslender.herovender.exception.HeroException;
import com.heroslender.herovender.helpers.MessageBuilder;
import com.heroslender.herovender.service.ShopService;
import lombok.val;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Comparator;

public class ShopController implements Controller {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Sell the player's inventory to the shops he has access to
     *
     * @param user The player selling the inventory
     * @return {@link Invoice} from the sell, or null if the {@link PlayerSellEvent} was cancelled
     */
    public Invoice sell(final User user) throws HeroException {
        val invoice = sellSilent(user);
        if (invoice == null) {
            // PlayerSellEvent was cancelled
            return null;
        }

        val message = invoice.getItems().isEmpty()
                ? HeroVender.getInstance().getMessageController().getMessage("sell.no-items")
                : HeroVender.getInstance().getMessageController().getMessage("sell.sold");

        message.ifPresent(msg -> {
            MessageBuilder messageBuilder = new MessageBuilder()
                    .withPlaceholder(user)
                    .withPlaceholder("item-count", invoice.getItemCount())
                    .withPlaceholder("price-formated", invoice.getTotal());

            user.sendMessage(messageBuilder.build(msg));
        });

        return invoice;
    }

    /**
     * Sell the player's inventory to the shops he has access to, withou sending him a message
     *
     * @param user The player selling the inventory
     * @return {@link Invoice} from the sell, or null if the {@link PlayerSellEvent} was cancelled
     */
    public Invoice sellSilent(final User user) throws HeroException {
        return sellSilent(user, shopService.get().stream()
                .filter(shop -> shop.getPermission() == null || user.getPlayer().hasPermission(shop.getPermission()))
                .sorted(Comparator.comparingInt(Shop::getPriority))
                .toArray(Shop[]::new));
    }

    /**
     * Sell the player's inventory, to the shops defined, withou sending him a message
     *
     * @param user  The player selling the inventory
     * @param shops The shops to sell to
     * @return {@link Invoice} from the sell, or null if the {@link PlayerSellEvent} was cancelled
     */
    public Invoice sellSilent(final User user, final Shop... shops) throws HeroException {
        user.checkDelay();

        val inventory = user.getInventory();
        val toSell = new ArrayList<SellItem>();

        for (Shop shop : shops) {
            for (int i = 0; i < inventory.getContents().length; i++) {
                val itemStack = inventory.getItem(i);
                val price = shop.getPrice(itemStack).orElse(-1);
                if (price <= 0) continue;
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
                toSell.add(new SellItem(itemStack, price, amount));
            }
        }

        val preInvoice = new Invoice(toSell);

        user.getSellBonus().ifPresent(bonus -> preInvoice.getBonuses().add(bonus));

        val event = new PlayerSellEvent(user.getPlayer(), preInvoice);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return null;

        val invoice = event.getInvoice();

        HeroVender.getInstance().getEconomy().depositPlayer(user.getPlayer(), invoice.getTotal());

        return invoice;
    }
}
