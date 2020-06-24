package com.heroslender.herovender.listener.autosell;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.SellDelayException;
import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.controller.UserController;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.utils.HeroException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AutoSellLogic {
    protected final UserController userController = HeroVender.getInstance().getUserController();
    protected final ShopController shopController = HeroVender.getInstance().getShopController();

    protected void autoSell(@NotNull final Player player) {
        Objects.requireNonNull(player, "player is null");

        final User user = userController.getOrCreate(player);

        if (user.isAutoSellActive() && user.isAbleToSell()) {
            autoSell(user);
        }
    }

    private void autoSell(final User user) {
        try {
            shopController.sell(user, false, true);
        } catch (SellDelayException ex) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroVender.getInstance(), () -> autoSell(user), ex.getDelay() / 50 + 1);
        } catch (HeroException ex) {
            user.sendMessage(ex.getMessage());
        }
    }
}
