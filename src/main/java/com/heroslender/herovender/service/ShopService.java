package com.heroslender.herovender.service;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.data.Shop;
import com.heroslender.herovender.data.ShopItem;
import com.heroslender.herovender.utils.items.MetaItemStack;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class ShopService implements Service<Shop> {
    private final List<Shop> shops = new ArrayList<>();

    @Override
    public void init() {
        final ConfigurationSection config = HeroVender.getInstance().getConfig().getConfigurationSection("shops");
        for (val shopId : config.getKeys(false)) {
            HeroVender.getInstance().getLogger().log(Level.INFO, "Loading the shop \"{0}\"...", shopId);
            val configShopItems = config.getStringList(shopId + ".items");
            if (configShopItems == null || configShopItems.isEmpty()) {
                HeroVender.getInstance().getLogger().log(Level.WARNING, "The shop \"{0}\" has no items defined!", shopId);
                continue;
            }

            val items = new ArrayList<ShopItem>();
            for (val shopItemString : configShopItems) {
                val metaItem = MetaItemStack.getFromString(shopItemString);
                if (metaItem == null) {
                    HeroVender.getInstance().getLogger().log(Level.WARNING, "Failed to load the item \"{0}\" from the config.", shopItemString);
                    continue;
                }

                val shopItem = new ShopItem(metaItem.getItemStack(), metaItem.getPrice(), metaItem.isIgnoreDurability());
                HeroVender.getInstance().getLogger().log(Level.INFO, " - [{0}] {1}",
                        new Object[]{shopId, shopItem});

                items.add(shopItem);
            }

            val permission = config.getString(shopId + ".permission");
            val priority = config.getInt(shopId + ".priority", -1);
            HeroVender.getInstance().getLogger().log(Level.INFO, "Shop \"{0}\"[perm=\"{1}\";priority=\"{2}\"] successfuly loaded",
                    new Object[]{shopId, permission, priority});
            shops.add(new Shop(shopId, permission, priority, items));
        }

        // Finish initialization after all shops have been loaded
        shops.forEach(shop -> shop.loadParents(this));

        sort();
    }

    /**
     * Sort the shop list.
     *
     * You must call this method it you manually add shops to the list.
     */
    public void sort() {
        shops.sort(Comparator.naturalOrder());
    }

    @Override
    public List<Shop> get() {
        return shops;
    }

    @Override
    public Optional<Shop> getById(String id) {
        for (Shop shop : shops) {
            if (shop.getName().equals(id)) {
                return Optional.of(shop);
            }
        }

        return Optional.empty();
    }

    @Override
    public void stop() {
        shops.clear();
    }
}
