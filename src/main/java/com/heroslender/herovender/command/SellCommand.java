package com.heroslender.herovender.command;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.CommandAllowsPlayersOnlyException;
import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.data.SellReason;
import com.heroslender.herovender.menu.impl.AutosellMenu;
import com.heroslender.herovender.menu.impl.ShopPricesMenu;
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

        if (args.length > 0) {
            String param = args[0];
            if (param.equalsIgnoreCase("prices")
                    || param.equalsIgnoreCase("price")
                    || param.equalsIgnoreCase("precos")
                    || param.equalsIgnoreCase("preco")) {
                new ShopPricesMenu(HeroVender.getInstance().getMessageController(), user);
            } else if (param.equalsIgnoreCase("menu")) {
                new AutosellMenu(HeroVender.getInstance().getMessageController(), user);
            }
        }

        shopController.sell(user, SellReason.COMMAND);
        return true;
    }
}
