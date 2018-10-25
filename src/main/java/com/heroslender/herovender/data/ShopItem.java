package com.heroslender.herovender.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class ShopItem {
    private final ItemStack itemStack;
    private final double price;
}
