package com.heroslender.herovender.menu.impl;

import com.google.common.collect.Lists;
import com.heroslender.herostackdrops.nms.ItemStackDeserializer;
import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.controller.MessageController;
import com.heroslender.herovender.data.Shop;
import com.heroslender.herovender.data.ShopItem;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.helpers.MessageBuilder;
import com.heroslender.herovender.menu.HItem;
import com.heroslender.herovender.menu.Menu;
import com.heroslender.herovender.utils.NumberUtil;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ShopPricesMenu extends Menu {
    private static final Comparator<ShopItem> PRICE_COMPARATOR_DESC = (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice());
    private static final HItem BORDER_GLASS = getBorderItem();
    private final MessageController messageController;

    public ShopPricesMenu(final MessageController messageController, final User user) {
        this(messageController, user, getShopItems(user), 1);
    }

    public ShopPricesMenu(final MessageController messageController, final User user, List<ShopItem> shopItems, int page) {
        super(messageController.getMessage("prices.menu.title").orElse("Sell Prices"), MenuSize.SIX_LINES);

        this.messageController = messageController;

        user.getPlayer().closeInventory();
        Bukkit.getScheduler().runTaskLater(HeroVender.getInstance(), () -> open(user.getPlayer()), 1);

        for (int i = 0; i < getSize(); i++) {
            if (i < 9 || i >= getSize() - 9) {
                setItem(i, BORDER_GLASS);
            } else if (i % 9 == 0) {
                setItem(i, BORDER_GLASS);
                setItem(i + 8, BORDER_GLASS);
            }
        }

        val messageBuilder = new MessageBuilder().withPlaceholder(user);
        if (page > 0) {
            val prevItem = getItemStack(
                    "prices.menu.prev-page",
                    messageBuilder,
                    new HItem(Material.ANVIL, "§aPrices", "", "§7Click here to see your sell prices!")
            );

            setItem(45, prevItem, e -> {
                new ShopPricesMenu(messageController, user, shopItems, page - 1);
            });
        }
        if ((page + 1) * 28 < shopItems.size()) {
            val nextItem = getItemStack(
                    "prices.menu.next-page",
                    messageBuilder,
                    new HItem(Material.ANVIL, "§aPrices", "", "§7Click here to see your sell prices!")
            );

            setItem(53, nextItem, e -> {
                new ShopPricesMenu(messageController, user, shopItems, page + 1);
            });
        }

        int index = page * 28;
        if (index <= shopItems.size()) {
            val items = shopItems.listIterator(index);
            val menuItems = getItems();
            for (int i = 0; i < menuItems.length && items.hasNext(); i++) {
                val item = menuItems[i];
                if (item != null) {
                    continue;
                }

                val shopItem = items.next();
                val itemStack = shopItem.getItemStack();
                messageController.getMessages("prices.menu.item.lore").ifPresent(loreMsg -> {
                    val meta = itemStack.getItemMeta();
                    List<String> newLore = Lists.newArrayList();

                    for (String s : loreMsg) {
                        if (s.equalsIgnoreCase(":lore:")) {
                            if (meta.hasLore()) {
                                newLore.addAll(meta.getLore());
                            }
                            continue;
                        }

                        newLore.add(
                                s.replace(":price:", Double.toString(shopItem.getPrice()))
                                        .replace(":price-formatted:", NumberUtil.formatShort(shopItem.getPrice()))
                        );
                    }

                    meta.setLore(newLore);
                    itemStack.setItemMeta(meta);
                });

                setItem(i, itemStack);
            }
        }
    }

    private static List<ShopItem> getShopItems(final User user) {
        final Shop[] shops = user.getShops();
        final List<ShopItem> items = Lists.newArrayList();

        for (Shop shop : shops) {
            for (ShopItem item : shop.getItems()) {
                boolean doAdd = true;
                Iterator<ShopItem> it = items.iterator();
                while (it.hasNext()) {
                    final ShopItem next = it.next();
                    if (next.isSimilar(item.getItemStack())) {
                        if (next.getPrice() <= item.getPrice()) {
                            it.remove();
                        } else {
                            doAdd = false;
                        }

                        break;
                    }
                }

                if (doAdd) {
                    items.add(item);
                }
            }
        }

        items.sort(PRICE_COMPARATOR_DESC);

        return items;
    }

    private static HItem getBorderItem() {
        try {
            return new HItem(Material.valueOf("STAINED_GLASS_PANE"), (short) 15, ChatColor.RESET.toString());
        } catch (IllegalArgumentException e) {
            return new HItem(Material.valueOf("BLACK_STAINED_GLASS_PANE"), ChatColor.RESET.toString());
        }
    }

    private ItemStack getItemStack(String messageId, MessageBuilder builder, ItemStack defaultItem) {
        ItemStackDeserializer itemStackDeserializer = HeroVender.getInstance().getNmsManager().getDeserializer();
        if (itemStackDeserializer == null) {
            return defaultItem;
        }

        val sellItemString = messageController.getMessage(messageId).orElse(null);
        val metaItemStack = itemStackDeserializer.deserialize(builder.build(sellItemString));

        return metaItemStack == null ? defaultItem : metaItemStack;
    }
}
