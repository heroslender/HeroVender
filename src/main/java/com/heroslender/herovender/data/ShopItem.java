package com.heroslender.herovender.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Utility;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class ShopItem {
    private final ItemStack itemStack;
    private final double price;
    private final boolean ignoreDurability;

    public ShopItem(ItemStack itemStack, double price) {
        this(itemStack, price, false);
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    /**
     * This method is the same as equals, but does not consider stack size
     * (amount).
     *
     * @param stack the item stack to compare to
     * @return true if the two stacks are equal, ignoring the amount and durability
     */
    @Utility
    public boolean isSimilar(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        return itemStack.getTypeId() == stack.getTypeId()
                && (ignoreDurability || itemStack.getDurability() == stack.getDurability())
                && itemStack.hasItemMeta() == stack.hasItemMeta()
                && (!itemStack.hasItemMeta() || Bukkit.getItemFactory().equals(itemStack.getItemMeta(), stack.getItemMeta()));
    }

    @Override
    public String toString() {
        return "ShopItem{" +
                "itemStack=" + itemStack.getType().name().toLowerCase().replace('_', ' ') +
                ", price=" + price +
                ", ignore-durability=" + ignoreDurability +
                '}';
    }
}
