package com.heroslender.herovender.nms.v1_8_R3;

import com.heroslender.herostackdrops.nms.ItemStackDeserializer;
import com.heroslender.herostackdrops.nms.ShopItemDto;
import com.heroslender.herovender.nms.v1_8_R3.items.MetaItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemStackDeserializerImpl implements ItemStackDeserializer {

    @Override
    public @Nullable ItemStack deserialize(String serialized) {
        ShopItemDto shopItemDto = deserializeShopItem(serialized);
        if (shopItemDto == null) {
            return null;
        }

        return shopItemDto.getItemStack();
    }

    @Override
    public @Nullable ShopItemDto deserializeShopItem(String serialized) {
        return MetaItemStack.getFromString(serialized);
    }
}
