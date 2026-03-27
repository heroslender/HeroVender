package com.github.heroslender.herovender.listener.autosell;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.command.exception.SellDelayException;
import com.github.heroslender.herovender.controller.ShopController;
import com.github.heroslender.herovender.controller.UserController;
import com.github.heroslender.herovender.data.SellReason;
import com.github.heroslender.herovender.data.User;
import com.github.heroslender.herovender.utils.HeroException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AutoSellLogic {
    protected final UserController userController = HeroVender.getInstance().getUserController();
    protected final ShopController shopController = HeroVender.getInstance().getShopController();

    protected void autoSell(@NotNull final Player player) {
        Objects.requireNonNull(player, "player is null");

        if (HeroVender.getInstance().getAutoSellManager().isAutoSellInvFull() && !isInventoryFull(player.getInventory())) {
            return;
        }

        final User user = userController.getOrCreate(player);

        if (user.isAutoSellActive()) {
            autoSell(user);
        }
    }

    protected boolean isInventoryFull(@NotNull Inventory inventory) {
        Objects.requireNonNull(inventory, "inventory is null");

        for (ItemStack stack : inventory.getContents()) {
            if (stack == null) {
                return false;
            }
        }

        return true;
    }

    private void autoSell(final User user) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(HeroVender.getInstance(), () -> autoSell(user));
            return;
        }

        try {
            shopController.sell(user, SellReason.AUTO);
        } catch (SellDelayException ex) {
            // TODO - Improve this
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroVender.getInstance(), () -> autoSell(user), ex.getDelay() / 50 + 1);
        } catch (HeroException ex) {
            user.sendMessage(ex.getMessage());
        }
    }
}
