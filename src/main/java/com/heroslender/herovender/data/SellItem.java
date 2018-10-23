package com.heroslender.herovender.data;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class SellItem extends ShopItem {
    @Getter private final int amount;

    public SellItem(ItemStack itemStack, double price, int amount) {
        super(itemStack, price);
        this.amount = amount;
    }
}
