package com.heroslender.herovender.nms.v1_8_R3;

import com.heroslender.herostackdrops.nms.Nms;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NmsImpl implements Nms {
    @Override
    public @Nullable ItemStack getItemInHand(Player player) {
        //noinspection deprecation
        return player.getItemInHand();
    }
}
