package com.github.heroslender.herovender.command;

import com.github.heroslender.herovender.Config;
import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.command.exception.CommandAllowsPlayersOnlyException;
import com.github.heroslender.herovender.helpers.MessageBuilder;
import com.github.heroslender.herovender.utils.HeroException;
import lombok.val;
import org.bukkit.command.CommandSender;

public class ShiftsellCommand extends Command {

    public ShiftsellCommand() {
        super("shiftsell");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) throws HeroException {
        if (!sender.hasPermission(Config.SHIFTSELL_PERMISSION)) {
            sender.sendMessage(getPermissionMessage());
            return;
        }

        val user = getUser(sender).orElseThrow(CommandAllowsPlayersOnlyException::new);

        user.toggleShiftSell();

        val messageOpt = user.isShiftSellActive()
                ? HeroVender.getInstance().getMessageController().getMessage("sell.shiftsell.on")
                : HeroVender.getInstance().getMessageController().getMessage("sell.shiftsell.off");

        messageOpt.ifPresent(message -> {
            val messageBuilder = new MessageBuilder()
                    .withPlaceholder(user);

            sender.sendMessage(messageBuilder.build(message));
        });
    }
}
