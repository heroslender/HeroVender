package com.heroslender.herostackdrops.nms;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemStackDeserializer {
    @Nullable ItemStack deserialize(String serialized);

    @Nullable ShopItemDto deserializeShopItem(String serialized);
}
