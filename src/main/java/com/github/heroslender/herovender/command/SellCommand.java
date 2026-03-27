package com.github.heroslender.herovender.command;

import com.github.heroslender.herovender.command.exception.CommandAllowsPlayersOnlyException;
import com.github.heroslender.herovender.command.exception.SellDelayException;
import com.github.heroslender.herovender.controller.ShopController;
import com.github.heroslender.herovender.data.SellReason;
import com.github.heroslender.herovender.utils.HeroException;
import lombok.val;
import org.bukkit.command.CommandSender;

public class SellCommand extends Command {
    private final ShopController shopController;

    public SellCommand(ShopController shopController) {
        super("sell");

        this.shopController = shopController;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) throws HeroException {
        val user = getUser(sender).orElseThrow(CommandAllowsPlayersOnlyException::new);

        try {
            shopController.sell(user, SellReason.COMMAND);
        } catch (SellDelayException e) {
            e.sendUserMessage();
        }
    }
}
