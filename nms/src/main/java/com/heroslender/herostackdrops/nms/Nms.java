package com.heroslender.herostackdrops.nms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface Nms {

    @Nullable ItemStack getItemInHand(Player player);
}
