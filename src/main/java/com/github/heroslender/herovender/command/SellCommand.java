package com.github.heroslender.herovender.command;

import com.github.heroslender.herovender.command.exception.CommandAllowsPlayersOnlyException;
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

//        if (args.length > 0) {
//            String param = args[0];
//            if (param.equalsIgnoreCase("prices")
//                    || param.equalsIgnoreCase("price")
//                    || param.equalsIgnoreCase("precos")
//                    || param.equalsIgnoreCase("preco")) {
//                new ShopPricesMenu(HeroVender.getInstance().getMessageController(), user);
//            } else if (param.equalsIgnoreCase("menu")) {
//                new AutosellMenu(HeroVender.getInstance().getMessageController(), user);
//            }
//        }

        shopController.sell(user, SellReason.COMMAND);
    }
}
