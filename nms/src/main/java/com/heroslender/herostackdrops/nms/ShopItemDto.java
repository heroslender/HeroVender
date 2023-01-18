package com.heroslender.herostackdrops.nms;

import org.bukkit.inventory.ItemStack;

public interface ShopItemDto {
    ItemStack getItemStack();

    double getPrice();

    boolean isIgnoreDurability();
}
