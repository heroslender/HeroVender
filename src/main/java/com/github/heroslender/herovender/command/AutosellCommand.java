package com.github.heroslender.herovender.command;

import com.github.heroslender.herovender.Config;
import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.command.exception.CommandAllowsPlayersOnlyException;
import com.github.heroslender.herovender.helpers.MessageBuilder;
import com.github.heroslender.herovender.utils.HeroException;
import lombok.val;
import org.bukkit.command.CommandSender;

public class AutosellCommand extends Command {

    public AutosellCommand() {
        super("autosell");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) throws HeroException {
        if (!sender.hasPermission(Config.AUTOSELL_PERMISSION)) {
            sender.sendMessage(getPermissionMessage());
            return;
        }

        val user = getUser(sender).orElseThrow(CommandAllowsPlayersOnlyException::new);

        user.toggleAutoSell();

        val messageOpt = user.isAutoSellActive()
                ? HeroVender.getInstance().getMessageController().getMessage("sell.autosell.on")
                : HeroVender.getInstance().getMessageController().getMessage("sell.autosell.off");

        messageOpt.ifPresent(message -> {
            val messageBuilder = new MessageBuilder()
                    .withPlaceholder(user);

            sender.sendMessage(messageBuilder.build(message));
        });
    }
}
