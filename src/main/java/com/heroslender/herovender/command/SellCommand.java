package com.heroslender.herovender.command;

import com.heroslender.herovender.command.exception.CommandAllowsPlayersOnlyException;
import com.heroslender.herovender.controller.ShopController;
import com.heroslender.herovender.helpers.CustomFileConfiguration;
import com.heroslender.herovender.utils.HeroException;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;

public class SellCommand extends Command {
    private final ShopController shopController;

    private final boolean chat;
    private final boolean actionbar;
    private final boolean ignoreEmpty;

    public SellCommand(ShopController shopController, Configuration messagesConfig) {
        super("sell");

        this.chat = messagesConfig.getBoolean("sell.command.chat", true);
        this.actionbar = messagesConfig.getBoolean("sell.command.actionbar", true);
        this.ignoreEmpty = messagesConfig.getBoolean("sell.command.ignore-empty", true);

        this.shopController = shopController;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) throws HeroException {
        val user = getUser(sender).orElseThrow(CommandAllowsPlayersOnlyException::new);

        shopController.sell(user, chat, actionbar, ignoreEmpty);
        return true;
    }
}
