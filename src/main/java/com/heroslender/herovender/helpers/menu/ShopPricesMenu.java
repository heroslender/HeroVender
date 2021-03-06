package com.heroslender.herovender.helpers.menu;

import com.google.common.collect.Lists;
import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.controller.MessageController;
import com.heroslender.herovender.data.Shop;
import com.heroslender.herovender.data.ShopItem;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.utils.NumberUtil;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ShopPricesMenu extends Menu {
    private static final Comparator<ShopItem> PRICE_COMPARATOR_DESC = (o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice());
    private static final HItem BORDER_GLASS = new HItem(Material.STAINED_GLASS_PANE, (short) 15, ChatColor.RESET.toString());

    public ShopPricesMenu(final MessageController messageController, final User user) {
        this(messageController, user, getShopItems(user));
    }

    public ShopPricesMenu(final MessageController messageController, final User user, List<ShopItem> shopItems) {
        super(messageController.getMessage("prices.menu.title").orElse("Sell Prices"), MenuSize.of(shopItems.size() + 18));

        for (int i = 0; i < getSize(); i++) {
            if (i < 9 || i >= getSize() - 9) {
                setItem(i, BORDER_GLASS);
            } else if (i % 9 == 0) {
                setItem(i, BORDER_GLASS);
                setItem(i + 8, BORDER_GLASS);
            }
        }

        val items = shopItems.iterator();
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

        Bukkit.getScheduler().runTask(HeroVender.getInstance(), () ->
                open(user.getPlayer())
        );
    }

    private static List<ShopItem> getShopItems(final User user) {
        final Shop[] shops = user.getShops();
        final List<ShopItem> items = Lists.newArrayList();

        for (Shop shop : shops) {
            for (ShopItem item : shop.getItems()) {
                Iterator<ShopItem> it = items.iterator();
                while (it.hasNext()) {
                    final ShopItem next = it.next();
                    if (next.isSimilar(item.getItemStack()) && next.getPrice() < item.getPrice()) {
                        it.remove();
                    }
                }

                items.add(item);
            }
        }

        items.sort(PRICE_COMPARATOR_DESC);

        return items;
    }
}
