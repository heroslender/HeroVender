package com.heroslender.herovender.command;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.exception.HeroException;
import com.heroslender.herovender.helpers.AutosellMenu;
import com.heroslender.herovender.utils.HeroCommand;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AutosellCommand extends HeroCommand {

    public AutosellCommand() {
        super("autosell");
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) throws HeroException {
        val user = getUser(sender).orElseThrow(() -> new HeroException(ChatColor.RED + "Only players are allowed to sell!"));

        new AutosellMenu(HeroVender.getInstance().getMessageController(), user);
        return true;
    }
}
