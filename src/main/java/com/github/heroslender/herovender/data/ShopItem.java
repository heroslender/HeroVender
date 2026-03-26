package com.github.heroslender.herovender.data;

import com.github.heroslender.herovender.HeroVender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Utility;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.logging.Level;

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

        return itemStack.getType() == stack.getType()
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

    public static ShopItem from(@Nullable ConfigurationSection config) {
        if (config == null) {
            return null;
        }

        ItemStack item;
        if (config.isString("item")) {
            val itemStr = config.getString("item");
            if (itemStr == null) {
                return null;
            }

            Material material = Material.matchMaterial(itemStr);
            if (material == null) {
                HeroVender.getInstance().getLogger().log(Level.WARNING, "The material \"{0}\" is not valid!", itemStr);
                return null;
            }

            item = new ItemStack(material);
        } else {
            item = config.getItemStack("item");
            if (item == null) {
                return null;
            }
        }

        val price = config.getDouble("price", 0.0);
        val ignoreDurability = config.getBoolean("ignore-durability", false);
        return new ShopItem(item, price, ignoreDurability);
    }
}
