package com.heroslender.herovender.command;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.CommandAllowsPlayersOnlyException;
import com.heroslender.herovender.utils.HeroException;
import com.heroslender.herovender.helpers.AutosellMenu;
import lombok.val;
import org.bukkit.command.CommandSender;

public class AutosellCommand extends Command {

    public AutosellCommand() {
        super("autosell");
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) throws HeroException {
        val user = getUser(sender).orElseThrow(CommandAllowsPlayersOnlyException::new);

        new AutosellMenu(HeroVender.getInstance().getMessageController(), user);
        return true;
    }
}
