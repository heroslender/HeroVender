package com.heroslender.herovender.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class HItem extends ItemStack {

    public HItem(ItemStack itemStack) {
        super(itemStack);
    }

    public HItem(Material type, String display) {
        super(type);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(display);
        setItemMeta(meta);
    }

    public HItem(Material type, String display, String... lore) {
        super(type);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(display);
        meta.setLore(Arrays.asList(lore));
        setItemMeta(meta);
    }

    public HItem(Material type, short data, String display) {
        super(type, 1, data);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(display);
        setItemMeta(meta);
    }

    public HItem(Material type, short data, String display, String... lore) {
        super(type, 1, data);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(display);
        meta.setLore(Arrays.asList(lore));
        setItemMeta(meta);
    }
}