package com.heroslender.herovender.command;

import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.exception.HeroException;
import com.heroslender.herovender.utils.HeroCommand;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SellCommand extends HeroCommand {
    private final ShopController shopController;

    public SellCommand(ShopController shopController) {
        super("sell");
        this.shopController = shopController;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) throws HeroException {
        val user = getUser(sender).orElseThrow(() -> new HeroException(ChatColor.RED + "Only players are allowed to sell!"));

        shopController.sell(user);
        return true;
    }
}
