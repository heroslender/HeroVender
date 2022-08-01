package com.heroslender.herovender.controller;

import com.google.common.collect.Lists;
import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.SellDelayException;
import com.heroslender.herovender.data.*;
import com.heroslender.herovender.event.PlayerSellEvent;
import com.heroslender.herovender.helpers.MessageBuilder;
import com.heroslender.herovender.service.ShopService;
import com.heroslender.herovender.utils.HeroException;
import com.heroslender.herovender.utils.NmsUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    public List<Shop> getShops() {
        return shopService.get();
    }

    public Shop[] getShops(User user) {
        final List<Shop> shops = shopService.get();
        final ArrayList<Shop> playerShops = Lists.newArrayList();
        for (Shop shop : shops) {
            if (shop.getPermission() == null || user.getPlayer().hasPermission(shop.getPermission())) {
                playerShops.add(shop);
            }
        }

        return playerShops.toArray(new Shop[0]);
    }

    /**
     * Sell the player's inventory to the shops he has access to
     *
     * @param user   The player selling the inventory
     * @param reason The reason for the sell
     * @return {@link Invoice} from the sell, or null if the {@link PlayerSellEvent} was cancelled
     */
    public Invoice sell(@NonNull User user, @NotNull SellReason reason) throws HeroException {
        Objects.requireNonNull(user, "user cannot be null");
        Objects.requireNonNull(reason, "reason cannot be null");

        val invoice = sellSilent(user, reason);
        if (reason.isChat() || reason.isActionbar() || !reason.isIgnoreEmpty()) {
            String toSend;
            if (invoice == null) {
                toSend = reason.isIgnoreEmpty()
                        ? null
                        : HeroVender.getInstance().getMessageController().getMessage("sell.no-items").orElse(null);
            } else {
                toSend = HeroVender.getInstance().getMessageController().getMessage("sell.sold").orElse(null);
                if (toSend != null) {
                    val messageBuilder = new MessageBuilder()
                            .withPlaceholder(user)
                            .withPlaceholder(invoice);

                    toSend = messageBuilder.build(toSend);
                }
            }

            if (toSend != null) {
                if (reason.isChat()) {
                    user.sendMessage(toSend);
                }
                if (reason.isActionbar()) {
                    NmsUtils.sendActionBar(toSend, user.getPlayer());
                }
            }
        }

        return invoice;
    }

    /**
     * Sell the player's inventory to the shops he has access to, without sending him a message
     *
     * @param user   The player selling the inventory
     * @param reason The reason for the sell
     * @return {@link Invoice} from the sell, or null if the {@link PlayerSellEvent} was cancelled
     */
    public Invoice sellSilent(@NonNull final User user, @NotNull final SellReason reason) throws HeroException {
        return sellSilent(user, reason, getShops(user));
    }

    /**
     * Sell the player's inventory, to the shops defined, without sending him a message
     *
     * @param user   The player selling the inventory
     * @param reason The reason for the sell
     * @param shops  The shops to sell to
     * @return {@link Invoice} from the sell, or null if the {@link PlayerSellEvent} was cancelled
     */
    public Invoice sellSilent(@NonNull final User user, @NotNull final SellReason reason, @NonNull final Shop... shops) throws SellDelayException {
        user.checkDelay();

        val inventory = user.getInventory();
        val toSell = new ArrayList<SellItem>();

        ShopItem cached = null;
        for (int i = 0; i < inventory.getContents().length; i++) {
            val itemStack = inventory.getItem(i);
            if (itemStack == null) {
                continue;
            }

            if (cached == null || !cached.isSimilar(itemStack)) {
                val shopItemOpt = getShopItem(itemStack, shops);
                if (shopItemOpt == null) {
                    continue;
                }

                cached = shopItemOpt;
            }

            inventory.setItem(i, null);

            int amount = itemStack.getAmount();

            // The item can be sold! Update them on the `to-sell` list
            for (SellItem sellingItem : toSell) {
                if (sellingItem.isSimilar(itemStack)) {
                    amount += sellingItem.getAmount();
                    toSell.remove(sellingItem);
                    break;
                }
            }
            toSell.add(new SellItem(itemStack, cached.getPrice(), amount));
        }

        val preInvoice = new Invoice(toSell, reason);
        if (!toSell.isEmpty()) {
            Bukkit.getPluginManager().callEvent(new PlayerSellEvent(user.getPlayer(), preInvoice));
            return null;
        }

        user.getSellBonus().ifPresent(bonus -> preInvoice.getBonuses().add(bonus));

        val event = new PlayerSellEvent(user.getPlayer(), preInvoice);
        Bukkit.getPluginManager().callEvent(event);

        val invoice = event.getInvoice();
        HeroVender.getInstance().getEconomy().depositPlayer(user.getPlayer(), invoice.getTotal());

        return invoice;
    }

    private ShopItem getShopItem(ItemStack itemStack, Shop[] shops) {
        val available = new ArrayList<ShopItem>();
        for (Shop shop : shops) {
            val item = shop.getShopItem(itemStack);
            if (item != null) {
                available.add(item);
            }
        }

        if (available.isEmpty()) {
            return null;
        } else if (available.size() == 1) {
            return available.get(0);
        }

        ShopItem highest = available.get(0);
        for (int i = 1; i < available.size(); i++) {
            val item = available.get(i);
            if (item.getPrice() > highest.getPrice()) {
                highest = item;
            }
        }

        return highest;
    }
}
