package com.github.heroslender.herovender.controller;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.Message;
import com.github.heroslender.herovender.command.exception.SellDelayException;
import com.github.heroslender.herovender.data.*;
import com.github.heroslender.herovender.event.PlayerSellEvent;
import com.github.heroslender.herovender.helpers.MessageBuilder;
import com.github.heroslender.herovender.service.ShopService;
import com.github.heroslender.herovender.utils.HeroException;
import com.github.heroslender.herovender.utils.NmsUtils;
import com.github.heroslender.herovender.utils.NumberUtil;
import com.google.common.collect.Lists;
import io.github.miniplaceholders.api.Expansion;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
        sendMessage(user, invoice, reason);

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
        user.checkDelay(reason);

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

        val preInvoice = new Invoice(toSell, user.getSellBonuses(), reason);
        val event = new PlayerSellEvent(user.getPlayer(), preInvoice);
        Bukkit.getPluginManager().callEvent(event);

        if (toSell.isEmpty()) {
            return null;
        }

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
            return available.getFirst();
        }

        ShopItem highest = available.getFirst();
        for (int i = 1; i < available.size(); i++) {
            val item = available.get(i);
            if (item.getPrice() > highest.getPrice()) {
                highest = item;
            }
        }

        return highest;
    }

    public void sendMessage(User user, Invoice invoice, SellReason reason) {
        String chatMsg;
        String actionMsg;
        switch (reason) {
            case COMMAND:
                if (invoice == null) {
                    chatMsg = Message.SellCommandChatNoItems;
                    actionMsg = Message.SellCommandActionbarNoItems;
                } else {
                    chatMsg = Message.SellCommandChatSold;
                    actionMsg = Message.SellCommandActionbarSold;
                }
                break;
            case AUTO:
                if (invoice == null) {
                    chatMsg = Message.SellAutoChatNoItems;
                    actionMsg = Message.SellAutoActionbarNoItems;
                } else {
                    chatMsg = Message.SellAutoChatSold;
                    actionMsg = Message.SellAutoActionbarSold;
                }
                break;
            case SHIFT:
                if (invoice == null) {
                    chatMsg = Message.SellShiftChatNoItems;
                    actionMsg = Message.SellShiftActionbarNoItems;
                } else {
                    chatMsg = Message.SellShiftChatSold;
                    actionMsg = Message.SellShiftActionbarSold;
                }
                break;
            default:
                chatMsg = null;
                actionMsg = null;
                break;
        }

        if (chatMsg != null) {
            Expansion expansion = Expansion.builder("internal")
                    .globalPlaceholder("invoice-total", (queue, ctx) -> Tag.selfClosingInserting(Component.text(NumberUtil.format(invoice != null ? invoice.getTotal() : 0.0))))
                    .globalPlaceholder("invoice-total-formatted", (queue, ctx) -> Tag.selfClosingInserting(Component.text(NumberUtil.formatShort(invoice != null ? invoice.getTotal() : 0.0))))
                    .globalPlaceholder("invoice-item-count", (queue, ctx) -> Tag.selfClosingInserting(Component.text(invoice != null ? invoice.getItemCount() : 0)))
                    .build();

            user.sendMiniMessage(chatMsg, expansion);
        }
        if (actionMsg != null) {
            Expansion expansion = Expansion.builder("internal")
                    .globalPlaceholder("invoice-total", (queue, ctx) -> Tag.selfClosingInserting(Component.text(NumberUtil.format(invoice != null ? invoice.getTotal() : 0.0))))
                    .globalPlaceholder("invoice-total-formatted", (queue, ctx) -> Tag.selfClosingInserting(Component.text(NumberUtil.formatShort(invoice != null ? invoice.getTotal() : 0.0))))
                    .globalPlaceholder("invoice-item-count", (queue, ctx) -> Tag.selfClosingInserting(Component.text(invoice != null ? invoice.getItemCount() : 0)))
                    .build();

            user.getPlayer().sendActionBar(MiniMessage.miniMessage().deserialize(actionMsg, user.getPlayer(), expansion.globalPlaceholders()));
        }
    }
}
