package com.github.heroslender.herovender.service;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.data.Shop;
import lombok.Getter;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ShopService implements Service {
    private final List<Shop> shops = new ArrayList<>();
    @Getter
    private final Logger logger = HeroVender.getInstance().getLogger();

    @Override
    public void init() {
        final ConfigurationSection config = HeroVender.getInstance().getConfig().getConfigurationSection("shops");
        if (config == null) {
            getLogger().log(Level.SEVERE, "No shops defined in the config.");
            return;
        }

        for (val shopId : config.getKeys(false)) {
            getLogger().log(Level.INFO, "Loading the shop \"{0}\"...", shopId);
            ConfigurationSection shopConfig = config.getConfigurationSection(shopId);
            if (shopConfig == null) {
                getLogger().log(Level.SEVERE, "Failed to load shop \"{0}\"", shopId);
                continue;
            }

            Shop shop = Shop.from(shopId, shopConfig);
            if (shop == null) {
                continue;
            }

            getLogger().log(Level.INFO, "Shop \"{0}\"[perm=\"{1}\";priority=\"{2}\";items=[{3}]] successfuly loaded",
                    new Object[]{shopId, shop.getPermission(), shop.getPriority(), shop.getItems().stream().map((shopItem -> (shopItem.getItemStack().getType().key().value()))).collect(Collectors.joining(", "))});
            shops.add(shop);
        }

        // Finish initialization after all shops have been loaded
        shops.forEach(shop -> shop.loadParents(this));

        sort();
    }

    /**
     * Sort the shop list.
     * <p>
     * You must call this method it you manually add shops to the list.
     */
    public void sort() {
        shops.sort(Comparator.naturalOrder());
    }

    public List<Shop> get() {
        return shops;
    }

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
