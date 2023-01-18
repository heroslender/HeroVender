package com.heroslender.herovender.nms.v1_13_R1;

import com.heroslender.herostackdrops.nms.Nms;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NmsImpl implements Nms {
    @Override
    public @Nullable ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }
}
