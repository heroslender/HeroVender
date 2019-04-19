package com.heroslender.herovender.command;

import com.heroslender.herovender.Config;
import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.CommandAllowsPlayersOnlyException;
import com.heroslender.herovender.helpers.MessageBuilder;
import com.heroslender.herovender.utils.HeroException;
import lombok.val;
import org.bukkit.command.CommandSender;

public class ShiftsellCommand extends Command {

    public ShiftsellCommand() {
        super("shiftsell");
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) throws HeroException {
        if (!sender.hasPermission(Config.SHIFTSELL_PERMISSION)) {
            sender.sendMessage(getPermissionMessage());
            return true;
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
        return true;
    }
}
