package com.github.heroslender.herovender.data;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.service.ShopService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Data
@AllArgsConstructor
public class Shop implements Comparable<Shop> {
    @NonNull
    private final String name;
    private final String permission;
    @NonNull
    private final int priority;
    @NonNull
    private final List<ShopItem> items;

    public void loadParents(final ShopService shopService) {
        List<String> parents = HeroVender.getInstance().getConfig().getStringList("shops." + getName() + ".inherits");

        if (parents == null || parents.isEmpty()) {
//            HeroVender.getInstance().getLogger().log(Level.INFO, "No parents found for the shop {}.", getName());
            return;
        }

        for (String parentId : parents) {
            shopService.getById(parentId).ifPresent(shop -> {
                for (ShopItem parentShopitem : shop.items) {
                    // Lookup if there's a similar item already registered here
                    for (ShopItem shopItem : new ArrayList<>(items)/* prevent a ConcurrentModificationException */) {
                        // if they're similar, lets get the highest value and update here
                        if (shopItem.isSimilar(parentShopitem.getItemStack()) && shopItem.getPrice() < parentShopitem.getPrice()) {
                            items.remove(shopItem);
                            items.add(parentShopitem);
                        }
                    }
                }
            });
        }
    }

    public ShopItem getShopItem(final ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        for (ShopItem item : items) {
            if (item.isSimilar(itemStack)) {
                return item;
            }
        }

        return null;
    }

    @Override
    public int compareTo(@NotNull Shop o) {
        return Integer.compare(this.getPriority(), o.getPriority());
    }

    public static Shop from(String shopId, ConfigurationSection config) {
        val configShopItems = config.getConfigurationSection("items");
        if (configShopItems == null) {
            HeroVender.getInstance().getLogger().log(Level.WARNING, "The shop \"{0}\" has no items defined!", shopId);
            return null;
        }

        val items = new ArrayList<ShopItem>();
        for (String itemId : configShopItems.getKeys(false)) {
            val itemSection = configShopItems.getConfigurationSection(itemId);
            val item = ShopItem.from(itemSection);
            if (item == null) {
                HeroVender.getInstance().getLogger().log(Level.WARNING, "The shop has an invalid item for \"{0}\".", itemId);
                continue;
            }

            items.add(item);
        }

        val permission = config.getString("permission");
        val priority = config.getInt("priority", -1);

        return new Shop(shopId, permission, priority, items);
    }
}
