package com.github.heroslender.herovender.autosell;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.command.exception.SellDelayException;
import com.github.heroslender.herovender.controller.ShopController;
import com.github.heroslender.herovender.controller.UserController;
import com.github.heroslender.herovender.data.SellReason;
import com.github.heroslender.herovender.data.User;
import com.github.heroslender.herovender.service.AutoSellService;
import com.github.heroslender.herovender.utils.HeroException;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@RequiredArgsConstructor
public class AutoSellLogic {
    protected final UserController userController;
    protected final ShopController shopController;
    protected final AutoSellService autoSellService;

    protected void autoSell(@NotNull final Player player) {
        Objects.requireNonNull(player, "player is null");

        if (autoSellService.isAutoSellInvFull() && !isInventoryFull(player.getInventory())) {
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
        BukkitTask autoSellTask = user.getAutoSellTask();
        if (autoSellTask == null) {
            return; // AutoSell is already scheduled for this user
        }

        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(HeroVender.getInstance(), () -> autoSell(user));
            return;
        }

        try {
            shopController.sell(user, SellReason.AUTO);
        } catch (SellDelayException ex) {
            BukkitTask task = Bukkit
                    .getScheduler()
                    .runTaskLater(HeroVender.getInstance(), () -> autoSell(user), ex.getDelay() / 50 + 1);
            user.setAutoSellTask(task);
        } catch (HeroException ex) {
            user.sendMessage(ex.getMessage());
        }
    }
}
