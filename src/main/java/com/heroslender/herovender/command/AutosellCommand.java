package com.heroslender.herovender.command;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.CommandAllowsPlayersOnlyException;
import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.helpers.menu.AutosellMenu;
import com.heroslender.herovender.utils.HeroException;
import lombok.val;
import org.bukkit.command.CommandSender;

public class SellCommand extends Command {
    private final ShopController shopController;

    public SellCommand(ShopController shopController) {
        super("sell");
        this.shopController = shopController;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) throws HeroException {
        val user = getUser(sender).orElseThrow(CommandAllowsPlayersOnlyException::new);

        if (args.length > 0 && args[0].equalsIgnoreCase("menu")) {
            new AutosellMenu(HeroVender.getInstance().getMessageController(), user);
            return true;
        }

        shopController.sell(user);
        return true;
    }
}
